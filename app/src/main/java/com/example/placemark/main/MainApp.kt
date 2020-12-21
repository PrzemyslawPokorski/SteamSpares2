package com.example.placemark.main

import android.app.Application
import com.example.placemark.models.PlacemarkModel
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {

    val placemarks = ArrayList<PlacemarkModel>()

    override fun onCreate() {
        placemarks.add(PlacemarkModel("One", "About one..."))
        placemarks.add(PlacemarkModel("Two", "About two..."))
        placemarks.add(PlacemarkModel("Three", "About three..."))

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}