package com.wit.steamspares.helpers

import android.content.Context
import android.util.Log
import androidx.annotation.UiThread
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.model.SteamAppModel
import com.wit.steamspares.models.GameModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.runBlocking
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.custom.async
import org.jetbrains.anko.info
import java.io.*
import java.net.URL
import kotlin.concurrent.thread

class jsonHelper : AnkoLogger{
    val gson = GsonBuilder().setPrettyPrinting().create()
    val JSON_FILE = "steamspares.json"
    val type = object : TypeToken<MutableList<GameModel>>() { }.type
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

    fun saveGamesToJson(games : List<GameModel>, context: Context){
        info { context }
        val jsonString = gson.toJson(games, type)
        try {

            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(JSON_FILE, Context.MODE_PRIVATE))
            outputStreamWriter.write(jsonString)
            outputStreamWriter.close()
        } catch (e: Exception) {
            Log.e("Error: ", "Cannot read file: " + e.toString());
        }
    }

    fun loadGamesFromJson(context: Context) : ArrayList<GameModel>{
        var jsonString = ""
        try {
            jsonString = context.openFileInput(JSON_FILE).bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException) {
            Log.e("Error: ", "file not found: " + e.toString());
        } catch (e: IOException) {
            Log.e("Error: ", "cannot read file: " + e.toString());
        }
        return gson.fromJson(jsonString, type)
    }

    fun fileExists(context: Context): Boolean {
        info { context }
        val file = context.getFileStreamPath(JSON_FILE)
        return file.exists()
    }
}