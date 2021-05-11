package com.wit.steamspares.main

import android.app.Application
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.StorageReference
import com.wit.steamspares.model.SteamAppModel
import com.wit.steamspares.models.GameMemStore
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class MainApp : Application(), AnkoLogger {
//    val gameMemStore = GameMemStore()
//    lateinit var steamAppStore : List<SteamAppModel>

    lateinit var currentUser: FirebaseUser
    lateinit var database: FirebaseDatabase

    override fun onCreate() {
        GameMemStore.context = this
        GameMemStore.getAppIds()

        super.onCreate()
        info("Debug: Placemark main app started")
    }
}