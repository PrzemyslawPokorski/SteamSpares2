package com.example.placemark.models

import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class PlacemarkMemStore : PlacemarkStore, AnkoLogger {

    val placemarks = ArrayList<PlacemarkModel>()

    override fun findAll(): List<PlacemarkModel> {
        return placemarks
    }

    override fun create(placemark: PlacemarkModel) {
        placemarks.add(placemark)
        logAll()
    }

    fun logAll() {
        placemarks.forEach{ info("${it}") }
    }
}