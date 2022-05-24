package com.example.doyo.activities

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.R
import com.example.doyo.contracts.EditContract
import com.example.doyo.databinding.ActivityProfileBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import com.example.doyo.toBitmap
import com.example.doyo.toMap
import io.socket.client.Socket
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hi!
        //hi...
        //More, know I got it so here we go (let's go)
        //You look like you could use some more
        //Know I got it and never running low (o-y-a-oooooooo)

        val respBody = JSONObject(intent.getStringExtra("data")!!)
        println(respBody.toString(2))

        AccountService.initAccount(respBody)
        SocketService.initSocket(respBody.getString("accessToken"))

        socket = SocketService.socket
        socket.connect()
        socket.on("connect") {
            println(socket.id())
            socket.on("hello") { args ->

                println(args[0])
            }
            socket.emit("hello", "Боже..........................")
        }

        lateinit var avatar: Bitmap
        if (respBody.has("icon")) {
            avatar = toBitmap(respBody.get("icon").toString())
        } else {
            //Default icon
            val drawable = VectorDrawableCompat.create(resources, R.drawable.default_avatar, this.theme)
            avatar = drawable?.toBitmap()!!
        }

        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editLauncher = registerForActivityResult(EditContract()) {
            when(it.result) {
                true -> {
                    println("GET RESULT FROM ACTIVITY: ${it.result}")
                }
                false -> {
                    println("GET RESULT FROM ACTIVITY: ${it.result}")
                }
            }
        }

        //Should create Account object to parse JSON response later
        binding.avatar.setImageBitmap(avatar)
        val account =  respBody.toMap()["account"] as Map<String, String>
        binding.username.text = account["username"]

        binding.signout.setOnClickListener {
            val sharedPref = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
            sharedPref.edit().remove("token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            socket.disconnect()
        }


        binding.find.setOnClickListener {
            socket.emit("init")
        }

        binding.create.setOnClickListener {
            val args = JSONObject()
            args.put("players_num", 3)
            args.put("frames_num", 3)
            args.put("game_time", 30000)
            socket.emit("init", args)
        }

        socket.on("init") { body ->
            val intent = Intent(this, RoomActivity::class.java).apply {
                putExtra("data", body[0].toString())
            }
            startActivity(intent)
        }


        binding.avatar.setOnClickListener {

        }


        //Для изменения никнейма ИЛИ аватарки HttpService.editData()
        //Постоянное возвращаемое поле "message" ("Success" или "No changes" в случае успеха и описание ошибки в случае хуйни)
        //HttpService.editData(this, username = "username") - ник (с возвращаемым доп полем "username")
        //HttpService.editData(this, icon = "binaryIcon") - аватарка (с возвращаемым доп полем "icon")
        //В случае ошибки так же содержит поле "code"

    }

    override fun onDestroy() {
        socket.disconnect()
        println("Socket disconnected!")
        super.onDestroy()
    }

}