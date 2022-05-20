package com.example.doyo.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doyo.databinding.ActivityProfileBinding
import com.example.doyo.services.HttpService
import com.example.doyo.services.SocketService
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

        SocketService.setSocket(respBody.get("accessToken").toString())
        socket = SocketService.getSocket()
        socket.connect()
        socket.on("connect") {
            println(socket.id())
            socket.on("hello") { args ->
                println(1)
            }
            socket.emit("hello", "Боже..........................")
        }

        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signout.setOnClickListener {
            val sharedPref = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
            sharedPref.edit().remove("token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

            socket.disconnect()
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