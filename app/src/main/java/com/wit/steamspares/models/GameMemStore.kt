package com.wit.steamspares.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.helpers.jsonHelper
import com.wit.steamspares.model.SteamAppModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class GameMemStore(val context : Context) : AnkoLogger, ViewModel() {
    val GAMES_FILE = "steamspares.json"
    val gameType = object : TypeToken<MutableList<GameModel>>() { }.type
    val steamAppType = object : TypeToken<MutableList<SteamAppModel>>() { }.type
    val STEAMAPP_FILE = "steamappids.json"
    var games = ArrayList<GameModel>()
    var gamesLD: MutableLiveData<List<GameModel>>? = null
    var steamList = ArrayList<SteamAppModel>()
    lateinit var jsonHelper : jsonHelper

//    fun findAll(): List<GameModel> {
//        gamesLD = MutableLiveData<ArrayList<GameModel>>()
//        if(steamList.count() == 0){
//            runBlocking {
//                jsonHelper = jsonHelper()
//                if (jsonHelper.fileExists(STEAMAPP_FILE, context) && jsonHelper.lastFileUpdate(STEAMAPP_FILE, context) < 60){
//                    steamList = jsonHelper.loadIdsFromJson(context)
//                }
//                else{
//                    steamList = jsonHelper.downloadSteamAppList() as ArrayList<SteamAppModel>
//                    jsonHelper.saveIdsToJson(steamList, context)
//                }
//            }
//        }
//
//        if(games.count() == 0 && jsonHelper.fileExists(GAMES_FILE, context))
//            games = jsonHelper.loadGamesFromJson(context)
//
//        return games
//    }

    fun findAll(): MutableLiveData<List<GameModel>>? {
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

        if(gamesLD == null && jsonHelper.fileExists(GAMES_FILE, context)){
            gamesLD = MutableLiveData<List<GameModel>>()
            gamesLD!!.value = jsonHelper.loadGamesFromJson(context)
        }

        return gamesLD
    }


    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = games.partition { it.status }
        return if(keyUsed) used else unused
    }

    fun create(name: String, code : String, status : Boolean, notes: String) {
        games.add(GameModel(findSteamId(name), name, code, status, notes))
        jsonHelper.saveGamesToJson(games, context)
        logAll()
    }

    fun update(id : Int, name: String, code : String, status : Boolean, notes: String?) {
        var foundGame: GameModel? = games.find { p -> p.id == id }
        if (foundGame != null) {
            foundGame.id = code.hashCode()
            foundGame.name = name
            foundGame.notes = notes
            foundGame.code = code
            foundGame.status = status
            foundGame.appid = findSteamId(name)
            foundGame.url = getGameUrl(foundGame.appid)
            foundGame.bannerUrl = getImageUrl(foundGame.appid)
            jsonHelper.saveGamesToJson(games, context)
            logAll()
        }
        else
            info { "Superdebug: Game $id, $name not found" }
    }

    fun delete(game: GameModel){
        games.remove(game)
        jsonHelper.saveGamesToJson(games, context)
    }

    fun getFiltered(query : String = "") : List<GameModel>{
        return games.filter { it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true }
    }

    fun getFiltered(query : String = "", usedStatus: Boolean) : List<GameModel>{
        return games.filter { (it.name.contains(query, ignoreCase = true) || it.notes?.contains(query, ignoreCase = true) ?: true) && it.status == usedStatus }
    }

    fun logAll() {
        games.forEach { info("Debug: ${it}") }
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
}