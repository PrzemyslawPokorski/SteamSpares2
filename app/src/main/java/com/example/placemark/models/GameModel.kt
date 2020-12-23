package com.example.placemark.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameModel(var appid : Int, var name: String = "Missing", var code : String, var status : Boolean, var notes : String? = null, var url : String? = "") : Parcelable{
    //Game code will always be unique so we can use that as internal storage id
    val id = code.hashCode()

    init{
        url = "https://store.steampowered.com/app/$appid"
        name = if (name.isEmpty()) "Name not given" else name
        code = if (code.isEmpty()) "Code not given" else code
    }
}