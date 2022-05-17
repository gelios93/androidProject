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
        //More, know I got it so here we go (let's go)
        //You look like you could use some more
        //Know I got it and never running low (o-y-a-oooooooo)

        val respBody = JSONObject(intent.getStringExtra("data"))
        println(respBody.toString(2))

//        respBody.has("icon")
//        respBody.get("icon")


        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signout.setOnClickListener {
            val sharedPref = this.getSharedPreferences(this.packageName, Context.MODE_PRIVATE)
            sharedPref.edit().remove("token").apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}