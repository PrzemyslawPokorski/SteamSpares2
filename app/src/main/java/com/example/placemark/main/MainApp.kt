package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.GameMemStore
import com.example.placemark.models.GameModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    val gameMemStore = GameMemStore()

    override fun onCreate() {
        gameMemStore.create(GameModel(name = "Game1", appid = 0, code = "NONE", status = false, notes = "About game1..."))
        gameMemStore.create(GameModel(name = "Game2", appid = 0, code = "NONE", status = false, notes = "About game2..."))
        gameMemStore.create(GameModel(name = "Game3", appid = 0, code = "NONE", status = true, notes = "About game3..."))
        gameMemStore.create(GameModel(name = "Game4", appid = 0, code = "NONE", status = false, notes = "About game4..."))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}