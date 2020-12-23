package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.GameMemStore
import com.example.placemark.models.GameModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    val placemarks = GameMemStore()

    override fun onCreate() {
        placemarks.create(GameModel(title = "One", description = "About one..."))
        placemarks.create(GameModel(title = "Two", description = "About two..."))
        placemarks.create(GameModel(title = "Three", description = "About three..."))
        placemarks.create(GameModel(title = "Four", description = "About four...", status = true))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}