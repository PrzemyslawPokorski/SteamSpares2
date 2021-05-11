package com.wit.steamspares.main

import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.wit.steamspares.model.SteamAppModel
import com.wit.steamspares.models.GameMemStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {
//    val gameMemStore = GameMemStore()

    lateinit var steamAppStore : List<SteamAppModel>
    lateinit var auth: FirebaseAuth

    override fun onCreate() {
//        gameMemStore.context = this
//        gameMemStore.findAll()
        GameMemStore.context = this
        GameMemStore.getAppIds()
//        gameMemStore.create(name = "Game1", code = "1111", status = false, notes = "About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1...")
//        gameMemStore.create(name = "Game2", code = "2222", status = false, notes = "About game2...")
//        gameMemStore.create(name = "Game3", code = "333", status = true, notes = "About game3...")
//        gameMemStore.create(name = "Game4", code = "444", status = false, notes = "About game4...")

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}