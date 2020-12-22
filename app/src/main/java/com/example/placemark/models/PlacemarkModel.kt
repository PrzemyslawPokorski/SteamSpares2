package com.example.placemark.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlacemarkModel(var title: String = "",
                          var description: String = "") : Parcelable