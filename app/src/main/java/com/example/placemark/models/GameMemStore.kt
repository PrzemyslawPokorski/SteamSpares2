package com.example.placemark.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class GameMemStore : GameStore, AnkoLogger {

    val games = ArrayList<GameModel>()

    override fun findAll(): List<GameModel> {
        return games
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = games.partition { it.status }
        return if(keyUsed) used else unused
    }


    override fun create(game: GameModel) {
        game.id = getId()
        games.add(game)
        logAll()
    }

    override fun update(game: GameModel) {
        var foundGame: GameModel? = games.find { p -> p.id == game.id }
        if (foundGame != null) {
            foundGame.title = game.title
            foundGame.description = game.description
            foundGame.image = game.image
            logAll()
        }
    }

    fun delete(game: GameModel){
        games.remove(game)
    }

    fun getFiltered(query : String = "") : List<GameModel>{
        return games.filter { it.title.contains(query, ignoreCase = true) }
    }

    fun getFiltered(query : String = "", usedStatus: Boolean) : List<GameModel>{
        return games.filter { it.title.contains(query, ignoreCase = true) && it.status == usedStatus}
    }

    fun logAll() {
        games.forEach { info("Debug: ${it}") }
    }
}