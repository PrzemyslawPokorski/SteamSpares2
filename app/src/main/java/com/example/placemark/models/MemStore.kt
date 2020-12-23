package com.example.placemark.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class GameMemStore : GameStore, AnkoLogger {

    val placemarks = ArrayList<GameModel>()

    override fun findAll(): List<GameModel> {
        return placemarks
    }

    fun getUsed(keyUsed : Boolean = true) : List<GameModel>{
        var (used, unused) = placemarks.partition { it.status }
        return if(keyUsed) used else unused
    }


    override fun create(game: GameModel) {
        game.id = getId()
        placemarks.add(game)
        logAll()
    }

    override fun update(game: GameModel) {
        var foundGame: GameModel? = placemarks.find { p -> p.id == game.id }
        if (foundGame != null) {
            foundGame.title = game.title
            foundGame.description = game.description
            foundGame.image = game.image
            logAll()
        }
    }

    fun delete(game: GameModel){
        placemarks.remove(game)
    }

    fun logAll() {
        placemarks.forEach { info("Debug: ${it}") }
    }
}