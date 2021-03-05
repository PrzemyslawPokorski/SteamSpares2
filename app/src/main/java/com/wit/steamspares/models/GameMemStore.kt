package com.wit.steamspares.models

import android.content.Context
import android.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.helpers.jsonHelper
import com.wit.steamspares.model.SteamAppModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class GameMemStore : AnkoLogger, ViewModel(),
    SearchView.OnQueryTextListener {
    val GAMES_FILE = "steamspares.json"
    val gameType = object : TypeToken<MutableList<GameModel>>() { }.type
    val steamAppType = object : TypeToken<MutableList<SteamAppModel>>() { }.type
    val STEAMAPP_FILE = "steamappids.json"
    var gamesLD = MutableLiveData<ArrayList<GameModel>>()
    val filterQuery = MutableLiveData<String>()
    var steamList = ArrayList<SteamAppModel>()
    lateinit var jsonHelper : jsonHelper
    lateinit var context: Context

    fun findAll(): MutableLiveData<ArrayList<GameModel>>? {
        if(steamList.count() == 0){
            runBlocking {
                jsonHelper = jsonHelper()
                if (jsonHelper.fileExists(STEAMAPP_FILE, context) && jsonHelper.lastFileUpdate(STEAMAPP_FILE, context) < 60){
                    steamList = jsonHelper.loadIdsFromJson(context)
                }
                else{
                    steamList = jsonHelper.downloadSteamAppList() as ArrayList<SteamAppModel>
                    jsonHelper.saveIdsToJson(steamList, context)
                }
            }
        }

        //TODO: Make sure this works with null safe etc
        info { "Debug: gamesLD val ${gamesLD.value}" }
        if(!jsonHelper.fileExists(GAMES_FILE, context)){
            jsonHelper.createFile(GAMES_FILE, context)
        }
        info { "Debug json file exists: ${jsonHelper.fileExists(GAMES_FILE, context)}" }
        if(gamesLD.value == null){
            info { "Debug loading to gamesLD" }
            gamesLD = MutableLiveData<ArrayList<GameModel>>()
            gamesLD!!.value = jsonHelper.loadGamesFromJson(context)
        }

        return gamesLD
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = gamesLD.value!!.partition { it.status }
        return if(keyUsed) used else unused
    }

    fun create(name: String, code : String, status : Boolean, notes: String) {
        val newVal = gamesLD.value?.apply { add(GameModel(findSteamId(name), name, code, status, notes)) }
        gamesLD.value = newVal!!

        info { "Debug gamesLD is ${gamesLD.value}" }
        jsonHelper.saveGamesToJson(gamesLD.value!!, context)
        logAll()
    }

    fun update(id : Int, name: String, code : String, status : Boolean, notes: String?) {
        var foundGame: GameModel? = gamesLD.value!!.find { p -> p.id == id }
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
            logAll()
        }
        else
            info { "Superdebug: Game $id, $name not found" }

        //Update the value for observers
        gamesLD.value = gamesLD.value
    }

    fun delete(game: GameModel){
        val newVal = gamesLD.value?.apply { remove(game) }

        //Update the value for observers
        gamesLD.value = newVal!!
        jsonHelper.saveGamesToJson(gamesLD.value!!, context)
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
}