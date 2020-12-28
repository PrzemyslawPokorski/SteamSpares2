package com.wit.steamspares.helpers

import androidx.annotation.UiThread
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.model.SteamAppModel
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.info
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class jsonHelper : AnkoLogger{
    val gson = GsonBuilder().setPrettyPrinting().create()
    var steamList = downloadSteamAppList()

    @UiThread
    fun downloadSteamAppList() : List<SteamAppModel>{
        info { "Debug: Downloading...." }
        var jsonString = ""
        try {
            runBlocking {
                info { "Debug: Blocking..." }

                jsonString =
                    URL("https://api.steampowered.com/ISteamApps/GetAppList/v0001/").readText()

            }
            jsonString = jsonString.drop(26)
            jsonString = jsonString.dropLast(3)
            val type = object : TypeToken<MutableList<SteamAppModel>>() { }.type
            return gson.fromJson(jsonString, type)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        return mutableListOf()
    }
}