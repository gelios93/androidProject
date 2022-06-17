package com.example.doyo.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.R
import com.example.doyo.SERVER_IP
import com.example.doyo.databinding.ActivityUserBinding
import com.example.doyo.fragments.GalleryFragment
import com.example.doyo.fragments.SearchFragment
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.HttpService
import com.example.doyo.services.SocketService
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class UserActivity: AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var user: User
    private lateinit var animations: List<String>

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)

        val jsonUser = intent?.extras?.get("user").toString()
        user = Gson().fromJson(jsonUser, User::class.java)

        if (AccountService.friends.contains(user)) {
            print("IS FRIEND")
            binding.btnSendRequest.isEnabled = false
        }

        SocketService.socket.emit("userInfo", user.username)
        SocketService.socket.on("userInfo") {data ->
            println("SOCKET ON")
            println(data[0].toString())
            if (data.isNotEmpty()) {
                val arrayString = JSONArray(data[0].toString()).toString()
                animations = (Gson().fromJson(arrayString, Array<String>::class.java)).toList()
                println(animations)
            }
            else
                animations = listOf()
            supportFragmentManager.beginTransaction().add(R.id.fragmentContainerView, GalleryFragment.newInstance(animations)).commit()
        }

        binding.btnSendRequest.isEnabled = true
        binding.username.text = user.username

        if (user.icon == " ") {
            val drawable = VectorDrawableCompat.create(this.resources, R.drawable.basic_icon, this.theme)
            binding.icon.setImageBitmap(drawable?.toBitmap()!!)
        }
        else
            Picasso.get().load("$SERVER_IP/doyo/images/icons/${user.icon}").into(binding.icon)

        binding.btnSendRequest.setOnClickListener{
            val response = HttpService.sendRequest(this, user.username)
            println(response.toString())
            if (response.has("code"))
                if (response.get("code") == 400) {
                    Toast.makeText(this, response.get("message").toString(), Toast.LENGTH_SHORT).show()
                }
            if (response.has("message"))
                if (response.get("message") == "success") {
                    Toast.makeText(this, "Friend request is sent!", Toast.LENGTH_SHORT).show()
                    binding.btnSendRequest.isEnabled = false
                }
                else
                    Toast.makeText(this, "Error while sending request", Toast.LENGTH_SHORT).show()
        }

        binding.btnToMenu.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketService.socket.off("userInfo")
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}
