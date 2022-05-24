package com.example.doyo.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.R
import com.example.doyo.contracts.EditContract
import com.example.doyo.databinding.ActivityProfileBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import io.socket.client.Socket
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {

    private lateinit var socket: Socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Less.

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

        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val editLauncher = registerForActivityResult(EditContract()) {
            when(it.result) {
                true -> {
                    binding.icon.setImageBitmap(AccountService.icon)
                }
                false -> {
                    if (AccountService.icon != null)
                        binding.icon.setImageBitmap(AccountService.icon)
                    else {
                        val drawable = VectorDrawableCompat.create(resources, R.drawable.default_avatar, this.theme)
                        binding.icon.setImageBitmap(drawable?.toBitmap()!!)
                    }
                }
            }
        }

        if (AccountService.icon != null) {
            binding.icon.setImageBitmap(AccountService.icon)
        } else {
            editLauncher.launch(EditContract.Input("new"))
        }

        binding.username.text = AccountService.username

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

        binding.icon.setOnClickListener {
            editLauncher.launch(EditContract.Input("edit"))
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