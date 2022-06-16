package com.example.doyo.activities

import com.example.doyo.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import com.example.doyo.databinding.RoomCoordinatorBinding
import com.example.doyo.models.Message
import com.example.doyo.models.User
import com.example.doyo.services.SocketService
import io.socket.client.Socket

class Coordinator : AppCompatActivity() {

    private val socket: Socket = SocketService.socket
    private lateinit var members : MutableList<User>
    private val messages : MutableList<Message> = mutableListOf()
    private lateinit var colors : MutableList<Int>
    private fun fillColors(){
        colors =
            mutableListOf(
                ContextCompat.getColor(this, R.color.pink_palette),
                ContextCompat.getColor(this, R.color.sky_blue_palette),
                ContextCompat.getColor(this, R.color.green_palette),
                ContextCompat.getColor(this, R.color.cyan_palette),
                ContextCompat.getColor(this, R.color.yellow_palette),
                ContextCompat.getColor(this, R.color.orange_palette))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = RoomCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}