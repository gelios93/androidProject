package com.example.doyo.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("username") val username: String,
    @SerializedName("icon") val icon: String,
    @SerializedName("experience") val experience: Int) {
}