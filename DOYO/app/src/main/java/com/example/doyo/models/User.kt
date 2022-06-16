package com.example.doyo.models

import android.graphics.Color
import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username") val username: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("experience") val experience: Int,
    var color: Int = 0) {
}