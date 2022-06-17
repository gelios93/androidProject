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
import com.example.doyo.fragments.SearchFragment
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.HttpService
import com.squareup.picasso.Picasso

class UserActivity: AppCompatActivity() {

    private lateinit var binding: ActivityUserBinding
    private lateinit var user: User

    override fun onCreate(
        savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //user = intent?.extras?.get("user").toString()
        //if (AccountService.friends.contains(user))
        //        binding.btnSendRequest.isEnabled=false

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
            Toast.makeText(this, "Error while sending request", Toast.LENGTH_SHORT).show()
        }
    }
}
