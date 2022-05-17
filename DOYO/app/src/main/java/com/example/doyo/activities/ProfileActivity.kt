package com.example.doyo.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.doyo.databinding.ActivityProfileBinding
import org.json.JSONObject

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //hi!
        //hi...
        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val respBody = JSONObject(intent.getStringExtra("data"))
        println(respBody.toString(2))

//        respBody.has("icon")
//        respBody.get("icon")

        binding.signout.setOnClickListener {
            val sharedPref = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
            sharedPref.edit().remove("token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}