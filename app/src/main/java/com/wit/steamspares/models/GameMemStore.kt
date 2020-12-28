package com.wit.steamspares.models

import android.content.Context
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

class GameMemStore(val context : Context) : AnkoLogger {
    var games = ArrayList<GameModel>()
    var steamList = ArrayList<SteamAppModel>()
    lateinit var jsonHelper : jsonHelper

    fun findAll(): List<GameModel> {
        runBlocking {
            jsonHelper = withContext(Dispatchers.IO){jsonHelper()}
            steamList = jsonHelper.steamList as ArrayList<SteamAppModel>
        }

        if(games.count() == 0 && jsonHelper.fileExists(context))
            games = jsonHelper.loadGamesFromJson(context)

        return games
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
        jsonHelper.fileExists(context)
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