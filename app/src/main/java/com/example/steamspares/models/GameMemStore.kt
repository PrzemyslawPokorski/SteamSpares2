package com.example.steamspares.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

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
}