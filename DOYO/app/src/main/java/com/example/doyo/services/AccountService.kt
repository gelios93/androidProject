package com.example.doyo.services

import android.graphics.Bitmap
import com.example.doyo.models.User
import com.example.doyo.toBitmap
import com.google.gson.Gson
import org.json.JSONObject

object AccountService {

    var icon: Bitmap? = null
    var experience: Int = 0

    lateinit var username: String
    lateinit var email: String

    lateinit var friends: MutableList<User>
    lateinit var requests: MutableList<User>
    lateinit var animations: MutableList<String>

    fun initAccount(responseBody: JSONObject) {
        icon = if (responseBody.has("icon")) toBitmap(responseBody.getString("icon")) else null

        val account = JSONObject(responseBody.get("account").toString())
        username = account.getString("username")
        email = account.getString("email")
        experience = account.getInt("experience")

        friends = Gson().fromJson(account.getString("friends"), Array<User>::class.java).toMutableList()
        requests = Gson().fromJson(account.getString("requests"), Array<User>::class.java).toMutableList()
        animations = Gson().fromJson(account.getString("animations"), Array<String>::class.java).toMutableList()

    }

}