package com.wit.steamspares.main

import android.app.Application
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import com.wit.steamspares.helpers.jsonHelper
import com.wit.steamspares.model.SteamAppModel
import com.wit.steamspares.models.GameMemStore
import com.wit.steamspares.models.GameModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.util.jar.Manifest

class MainApp : Application(), AnkoLogger {
    val gameMemStore = GameMemStore(this)

    lateinit var steamAppStore : List<SteamAppModel>

    override fun onCreate() {
        gameMemStore.findAll()
//        gameMemStore.create(name = "Game1", code = "1111", status = false, notes = "About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1About game1...")
//        gameMemStore.create(name = "Game2", code = "2222", status = false, notes = "About game2...")
//        gameMemStore.create(name = "Game3", code = "333", status = true, notes = "About game3...")
//        gameMemStore.create(name = "Game4", code = "444", status = false, notes = "About game4...")

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}