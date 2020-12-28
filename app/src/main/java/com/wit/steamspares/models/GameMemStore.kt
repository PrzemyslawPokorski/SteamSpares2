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

class GameMemStore : GameStore, AnkoLogger {
    val games = ArrayList<GameModel>()
    var steamList = ArrayList<SteamAppModel>()
    var jsonHelper : jsonHelper

    init {
        runBlocking {
            jsonHelper = withContext(Dispatchers.IO){jsonHelper()}
            steamList = jsonHelper.steamList as ArrayList<SteamAppModel>
        }
    }

    override fun findAll(): List<GameModel> {
        return games
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = games.partition { it.status }
        return if(keyUsed) used else unused
    }

    override fun create(game: GameModel) {
        game.appid = findSteamId(game.name)
        games.add(game)
        logAll()
    }

    override fun update(game: GameModel) {
        var foundGame: GameModel? = games.find { p -> p.id == game.id }
        if (foundGame != null) {
            foundGame.name = game.name
            foundGame.notes = game.notes
            foundGame.code = game.code
            foundGame.status = game.status
            foundGame.appid = findSteamId(game.name)
            foundGame.url = getGameUrl(foundGame.appid)
            foundGame.bannerUrl = getImageUrl(foundGame.appid)
            info { "Debug: steamappid found: ${foundGame.appid}" }
            logAll()
        }
    }

    override fun delete(game: GameModel){
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
        return "https://cdn.cloudflare.steamstatic.com/steam/apps/${id}/header_alt_assets_3.jpg"
    }
}