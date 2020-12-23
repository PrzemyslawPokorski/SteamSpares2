package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.GameMemStore
import com.example.placemark.models.GameModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    val gameMemStore = GameMemStore()

    override fun onCreate() {
        gameMemStore.create(GameModel(title = "One", description = "About one..."))
        gameMemStore.create(GameModel(title = "Two", description = "About two..."))
        gameMemStore.create(GameModel(title = "Three", description = "About three..."))
        gameMemStore.create(GameModel(title = "Four", description = "About four...", status = true))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}