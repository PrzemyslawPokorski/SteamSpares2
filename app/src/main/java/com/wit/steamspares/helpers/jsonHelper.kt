package com.wit.steamspares.helpers

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.annotation.UiThread
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.wit.steamspares.model.SteamAppModel
import com.wit.steamspares.models.GameModel
import kotlinx.coroutines.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import java.io.*
import java.net.URL
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

class jsonHelper : AnkoLogger{
    val gson = GsonBuilder().setPrettyPrinting().create()
    val GAMES_FILE = "steamspares.json"
    val gamesType = object : TypeToken<MutableList<GameModel>>() { }.type
    val APPIDS_FILE = "steamappids.json"
    val appidsType = object : TypeToken<MutableList<SteamAppModel>>() { }.type

    @UiThread
    fun downloadSteamAppList() : List<SteamAppModel>{
        info { "Debug: Downloading...." }
        var jsonString = ""
        try {
            runBlocking {
                info { "Debug: Blocking..." }

                jsonString = withContext(Dispatchers.IO) {
                    URL("https://api.steampowered.com/ISteamApps/GetAppList/v0001/").readText()
                }

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
        val jsonString = gson.toJson(games, gamesType)
        try {

            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(GAMES_FILE, Context.MODE_PRIVATE))
            outputStreamWriter.write(jsonString)
            outputStreamWriter.close()
        } catch (e: Exception) {
            Log.e("Error: ", "Cannot read file: " + e.toString());
        }
    }

    fun loadGamesFromJson(context: Context) : ArrayList<GameModel>{
        var jsonString = ""
        try {
            jsonString = context.openFileInput(GAMES_FILE).bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException) {
            Log.e("Error: ", "file not found: " + e.toString());
        } catch (e: IOException) {
            Log.e("Error: ", "cannot read file: " + e.toString());
        }
        return gson.fromJson(jsonString, gamesType)?: ArrayList<GameModel>()
    }

    fun saveIdsToJson(games : List<SteamAppModel>, context: Context){
        val jsonString = gson.toJson(games, appidsType)
        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput(APPIDS_FILE, Context.MODE_PRIVATE))
            outputStreamWriter.write(jsonString)
            outputStreamWriter.close()
        } catch (e: Exception) {
            Log.e("Error: ", "Cannot read file: " + e.toString());
        }
    }

    fun loadIdsFromJson(context: Context) : ArrayList<SteamAppModel>{
        var jsonString = ""
        try {
            jsonString = context.openFileInput(APPIDS_FILE).bufferedReader().use { it.readText() }
        } catch (e: FileNotFoundException) {
            Log.e("Error: ", "file not found: " + e.toString());
        } catch (e: IOException) {
            Log.e("Error: ", "cannot read file: " + e.toString());
        }
        return gson.fromJson(jsonString, appidsType)
    }

    fun fileExists(file : String, context: Context): Boolean {
        val file = context.getFileStreamPath(file)
        return file.exists()
    }

    fun lastFileUpdate(file : String, context: Context) : Long //Return difference in hours
    {
        val fileMod = context.getFileStreamPath(file).lastModified()
        val now = Instant.now().toEpochMilli()

        val diff: Long = Date(now).getTime() - Date(fileMod).getTime()
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        info { "Last steam app ids update: $minutes minutes ago" }

        return minutes
    }

    fun createFile(fileName: String, context: Context) {
        info { "Debug: files dir: ${context.getFilesDir()}" }
        val file = File("${context.filesDir}/${fileName}")
        file.createNewFile()
    }
}