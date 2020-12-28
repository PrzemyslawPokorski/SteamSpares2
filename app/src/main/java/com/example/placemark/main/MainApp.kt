package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.GameMemStore
import com.example.placemark.models.GameModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainApp : Application(), AnkoLogger {
    val gameMemStore = GameMemStore()

    override fun onCreate() {
        gameMemStore.create(GameModel(name = "Game1", appid = 457140, code = "1111", status = false, notes = "About game1..."))
        gameMemStore.create(GameModel(name = "Game2", appid = 0, code = "2222", status = false, notes = "About game2..."))
        gameMemStore.create(GameModel(name = "Game3", appid = 0, code = "3333", status = true, notes = "About game3..."))
        gameMemStore.create(GameModel(name = "Game4", appid = 0, code = "4444", status = false, notes = "About game4..."))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}