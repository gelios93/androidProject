package com.example.doyo.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doyo.databinding.ActivityLoginBinding

class LoginActivity(var email: String = "", var password: String = "") : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}