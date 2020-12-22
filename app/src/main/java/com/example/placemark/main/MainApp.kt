package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.PlacemarkMemStore
import com.example.placemark.models.PlacemarkModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    val placemarks = PlacemarkMemStore()

    override fun onCreate() {
        placemarks.create(PlacemarkModel("One", "About one..."))
        placemarks.create(PlacemarkModel("Two", "About two..."))
        placemarks.create(PlacemarkModel("Three", "About three..."))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}