package com.wit.steamspares.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GameModel(var appid : Int, var name: String = "Missing", var code : String, var status : Boolean, var notes : String? = null, var url : String? = "", var bannerUrl : String? = "") : Parcelable{
    //Game code will always be unique so we can use that as internal storage id
    val id = code.hashCode()

    init{
        url = "https://store.steampowered.com/app/$appid"
        bannerUrl = "https://cdn.cloudflare.steamstatic.com/steam/apps/${appid}/header_alt_assets_3.jpg"
        name = if (name.isEmpty()) "Name not given" else name
        code = if (code.isEmpty()) "Code not given" else code
    }
}