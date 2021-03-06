package com.wit.steamspares.models

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.R
import com.wit.steamspares.helpers.jsonHelper
import com.wit.steamspares.main.MainApp
import com.wit.steamspares.model.SteamAppModel
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import kotlinx.android.synthetic.main.home.*

object GameMemStore : AnkoLogger, ViewModel(),
    SearchView.OnQueryTextListener {
    var GAMES_FILE = "steamspares.json"
    val gameType = object : TypeToken<MutableList<GameModel>>() { }.type
    val steamAppType = object : TypeToken<MutableList<SteamAppModel>>() { }.type
    val STEAMAPP_FILE = "steamappids.json"
    var gamesLD = MutableLiveData<ArrayList<GameModel>>()
    val filterQuery = MutableLiveData<String>()
    var steamList = ArrayList<SteamAppModel>()
    lateinit var jsonHelper : jsonHelper
    lateinit var context: Context
    lateinit var mainAppFrame : View
    lateinit var dbRef : DatabaseReference

    fun setUser(user : FirebaseUser?){
        val database = FirebaseDatabase.getInstance()
        if(user == null) {
            dbRef = database.getReference("invalid-path")
            unloadGames()
            GAMES_FILE = "steamspares.json"
            return
        }

        dbRef = database.getReference("users/${user.uid}/games")

        val listener = object : ValueEventListener, ChildEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.w(TAG, "loadGames:onCancelled", error.toException())
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                update(readGameFromDB(snapshot))
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                update(readGameFromDB(snapshot))
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                update(readGameFromDB(snapshot))
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                delete(readGameFromDB(snapshot))
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                update(readGameFromDB(snapshot))
            }

        }
        dbRef.addChildEventListener(listener)


        GAMES_FILE = "${user.email}_steamspares.json"
    }

    fun getAppIds(){
        /**
         * On first launch: Load cached app id list.
         * If out of date or missing - block the thread and download the newest version
         */
        jsonHelper = jsonHelper()
        if(steamList.count() == 0){
            GlobalScope.launch {
                delay(100L)
                var downloaded = false

                this.launch {
                    delay(10000L)
                    if (!downloaded) {
                        val sb = Snackbar.make(
                            mainAppFrame,
                            R.string.connection_problem,
                            Snackbar.LENGTH_INDEFINITE
                        )
                        sb.setBackgroundTint(Color.RED)
                        sb.setTextColor(Color.WHITE)
                        sb.show()
                    }
                }

                //If app id file exists and it's less than an hour old
                if (jsonHelper.fileExists(STEAMAPP_FILE, context) && jsonHelper.lastFileUpdate(STEAMAPP_FILE, context) < R.integer.steam_app_id_timeout){
                    steamList = jsonHelper.loadIdsFromJson(context)
                    downloaded = true
                    info { "Debug4: Downloaded true" }
                }
                //If no file for app id or out of date
                else{
                    info { "GameMemStore downloading new steam app id list" }
                    steamList = jsonHelper.downloadSteamAppList() as ArrayList<SteamAppModel>
                    jsonHelper.saveIdsToJson(steamList, context)
                    downloaded = true
                    info { "Debug4: Downloaded true" }
                }
            }
        }
    }

    fun getGames(): MutableLiveData<ArrayList<GameModel>>? {
        //TODO: Make sure this works with null safe etc
        info { "Debug:$GAMES_FILE gamesLD val ${gamesLD.value}" }
        if(!jsonHelper.fileExists(GAMES_FILE, context)){
            jsonHelper.createFile(GAMES_FILE, context)
        }
        info { "Debug $GAMES_FILE json file exists: ${jsonHelper.fileExists(GAMES_FILE, context)}" }
        if(gamesLD.value == null || gamesLD.value!!.isEmpty()){
            info { "Debug $GAMES_FILE loading to gamesLD" }
            gamesLD = MutableLiveData<ArrayList<GameModel>>()
            gamesLD!!.value = jsonHelper.loadGamesFromJson(context)
        }

        info { "Debug4: Retrieved game list for $GAMES_FILE" }
        pushAllGamesToDB()
        return gamesLD
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = gamesLD.value!!.partition { it.status }
        return if(keyUsed) used else unused
    }

    fun create(name: String, code : String, status : Boolean, notes: String?) {
        val newGame = GameModel(findSteamId(name), name, code, status, notes)
        val newVal = gamesLD.value?.apply { add(newGame) }
        gamesLD.value = newVal!!

        info { "Debug $GAMES_FILE gamesLD is ${gamesLD.value}" }
        jsonHelper.saveGamesToJson(gamesLD.value!!, context)
        dbRef.child(newGame.id.toString()).setValue(newGame)
        logAll()
    }

    fun create(game : GameModel) {
        val newVal = gamesLD.value?.apply { add(game) }
        gamesLD.value = newVal!!

        info { "Debug $GAMES_FILE gamesLD is ${gamesLD.value}" }
        jsonHelper.saveGamesToJson(gamesLD.value!!, context)
        dbRef.child(game.id.toString()).setValue(game)
        logAll()
    }

    fun update(id : Int, name: String, code : String, status : Boolean, notes: String?) {
        val foundGame: GameModel? = gamesLD.value!!.find { p -> p.id == id }
        if (foundGame != null) {
            foundGame.id = code.hashCode()
            foundGame.name = name
            foundGame.notes = notes
            foundGame.code = code
            foundGame.status = status
            foundGame.appid = findSteamId(name)
            foundGame.url = getGameUrl(foundGame.appid)
            foundGame.bannerUrl = getImageUrl(foundGame.appid)
            jsonHelper.saveGamesToJson(gamesLD.value!!, context)
            dbRef.child(foundGame.id.toString()).setValue(foundGame)
            logAll()
        }
        else
            create(name, code, status, notes)

        //Update the value for observers
        gamesLD.value = gamesLD.value
    }

    fun update(game : GameModel) {
        val foundGame: GameModel? = gamesLD.value!!.find { p -> p.id == game.id }
        if (foundGame != null) {
            foundGame.id = game.id
            foundGame.name = game.name
            foundGame.notes = game.notes
            foundGame.code = game.code
            foundGame.status = game.status
            foundGame.appid = game.appid
            foundGame.url = game.url
            foundGame.bannerUrl = game.bannerUrl
            jsonHelper.saveGamesToJson(gamesLD.value!!, context)
            dbRef.child(foundGame.id.toString()).setValue(foundGame)
            logAll()
        }
        else
            create(game)

        //Update the value for observers
        gamesLD.value = gamesLD.value
    }

    fun delete(game: GameModel){
        val newVal = gamesLD.value?.apply { remove(game) }

        //Update the value for observers
        gamesLD.value = newVal!!
        jsonHelper.saveGamesToJson(gamesLD.value!!, context)
        dbRef.child(game.id.toString()).removeValue()
    }

    fun getFiltered(query : String = "") : List<GameModel>{
        return gamesLD.value!!.filter { it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true }
    }

    fun getFiltered(query : String = "", usedStatus: Boolean) : List<GameModel>{
        return gamesLD.value!!.filter { (it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true) && it.status == usedStatus }
    }

    fun logAll() {
        gamesLD.value!!.forEach { info("Debug: ${it}") }
        info { "------------------------------------------------------------------------------------------------------------------------------------------------------" }
    }

    fun findSteamId(name : String, idsUpdated : Boolean = false) : Int{
        //Filter down to names containing
        val apps = steamList.filter { it.name.contains(name, ignoreCase = true) }
        val app = apps.find { it.name.equals(name, ignoreCase = true) }
        //If exact match exists, get appid
        if (app != null) {
            return app.appid
        }
        //Else return "closest match" (first that was similar based on contain)
        if(apps.isNotEmpty())
            return apps[0].appid
        else
            if(!idsUpdated && jsonHelper.lastFileUpdate(STEAMAPP_FILE, context) >= 60)
                return findSteamId(name, true)

        return 0
    }

    fun getGameUrl(id : Int) : String{
        return "https://store.steampowered.com/app/$id"
    }

    fun getImageUrl(id : Int) : String{
        return "https://cdn.cloudflare.steamstatic.com/steam/apps/${id}/header.jpg"
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        filterQuery.value = newText!!
        return true
    }

    fun unloadGames() {
        gamesLD.value?.clear()
    }

    fun pushAllGamesToDB(){
        for(game in gamesLD.value!!){
            dbRef.child(game.id.toString()).setValue(game)
        }
    }

    fun updateGameFromDB(game : GameModel){
        val foundGame: GameModel? = gamesLD.value!!.find { p -> p.id == game.id }

        if (foundGame == null) {
            info { "Child changed, but no local game for this id found" }
            create(game.name, game.code, game.status, game.notes)
        }
        else if(foundGame != game)
            update(game.id, game.name, game.code, game.status, game.notes)
    }

    fun readGameFromDB(entry : DataSnapshot) : GameModel{
        val appid = (entry.child("appid").value as Long).toInt()
        val bannerUrl = entry.child("bannerUrl").value as String
        val code = entry.child("code").value as String
        val id = (entry.child("id").value as Long).toInt()
        val name = entry.child("name").value as String
        val notes = entry.child("notes").value as String
        val status = entry.child("status").value as Boolean
        val url = entry.child("url").value as String

        return GameModel(appid, name, code, status, notes, url, bannerUrl)
    }
}