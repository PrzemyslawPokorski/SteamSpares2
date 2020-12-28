package com.wit.steamspares.models

import com.wit.steamspares.helpers.jsonHelper
import com.wit.steamspares.model.SteamAppModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.info
import kotlin.concurrent.thread

class GameMemStore : AnkoLogger {
    val games = ArrayList<GameModel>()
    var steamList = ArrayList<SteamAppModel>()
    var jsonHelper : jsonHelper

    init {
        runBlocking {
            jsonHelper = withContext(Dispatchers.IO){jsonHelper()}
            steamList = jsonHelper.steamList as ArrayList<SteamAppModel>
        }
    }

    fun findAll(): List<GameModel> {
        return games
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = games.partition { it.status }
        return if(keyUsed) used else unused
    }

    fun create(name: String, code : String, status : Boolean, notes: String) {
        games.add(GameModel(findSteamId(name), name, code, status, notes))
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
            logAll()
        }
        else
            info { "Superdebug: Game $id, $name not found" }
    }

    fun delete(game: GameModel){
        games.remove(game)
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

    fun findSteamId(name : String) : Int{
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

        return 0
    }

    fun getGameUrl(id : Int) : String{
        return "https://store.steampowered.com/app/$id"
    }

    fun getImageUrl(id : Int) : String{
        return "https://cdn.cloudflare.steamstatic.com/steam/apps/${id}/header.jpg"
    }
}