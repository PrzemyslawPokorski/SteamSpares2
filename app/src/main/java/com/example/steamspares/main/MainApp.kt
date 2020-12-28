package com.example.steamspares.main

import android.app.Application
import com.example.steamspares.models.GameMemStore
import com.example.steamspares.models.GameModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {
    val gameMemStore = GameMemStore()

    override fun onCreate() {
        gameMemStore.create(GameModel(name = "Game1", appid = 457140, code = "1111", status = false, notes = "About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1..."))
        gameMemStore.create(GameModel(name = "Game2", appid = 0, code = "2222", status = false, notes = "About game2..."))
        gameMemStore.create(GameModel(name = "Game3", appid = 0, code = "3333", status = true, notes = "About game3..."))
        gameMemStore.create(GameModel(name = "Game4", appid = 0, code = "4444", status = false, notes = "About game4..."))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}