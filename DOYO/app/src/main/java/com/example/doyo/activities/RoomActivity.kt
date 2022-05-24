package com.example.doyo.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.doyo.R
import com.example.doyo.adapters.MemberListAdapter
import com.example.doyo.adapters.MessageListAdapter
import com.example.doyo.databinding.ActivityRoomBinding
import com.example.doyo.models.Message
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject

import java.util.*

class RoomActivity : AppCompatActivity() {

    private lateinit var members : MutableList<User>
    private val messages : MutableList<Message> = mutableListOf()

    private val socket: Socket = SocketService.socket

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val respBody = JSONObject(intent.getStringExtra("data")!!)
        println(respBody.toString(2))

        val binding = ActivityRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        members =
            if (respBody.has("players"))
                Gson().fromJson(respBody.getString("players"), Array<User>::class.java).toMutableList()
            else
                mutableListOf(User(AccountService.username, " ", AccountService.experience))

        val membersAdapter = MemberListAdapter(layoutInflater)
        binding.members.adapter = membersAdapter
        binding.members.setHasFixedSize(true)
        binding.members.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        membersAdapter.submitList(members.toList())

//        messages.add(Message("ITS ME", members[0].username, "${Date().hours}:${Date().minutes}"))
        val messagesAdapter = MessageListAdapter(layoutInflater, messages)
        binding.chat.adapter = messagesAdapter
        binding.chat.setHasFixedSize(true)
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
//        manager.reverseLayout = true
        binding.chat.layoutManager =  manager

        binding.chatSend.setOnClickListener {
            if (binding.chatEdit.text.toString() != ""){
                val mes = JSONObject()
                mes.put("username", AccountService.username)
                mes.put("value", binding.chatEdit.text.toString())

                socket.emit("message", mes.toString())
                binding.chatEdit.setText("")
//                binding.chat.scrollToPosition((messages.size))
            }
        }

        socket.on("message") { arg ->
            println("MESSAGE")
            val newMessage = JSONObject(arg[0].toString())
            val date = Date()
            messages.add(Message(newMessage.getString("value"), newMessage.getString("username"), "${date.hours}:${date.minutes}"))
            messagesAdapter.notifyItemInserted(messages.size)
            Handler(Looper.getMainLooper()).post {
                binding.chat.scrollToPosition(messages.size-1)
            }
        }


        binding.search.setOnClickListener {
            socket.emit("search", respBody)
            binding.progressBar.visibility = View.VISIBLE
        }


        socket.on("add") { arg ->
            val newMember = Gson().fromJson(arg[0].toString(), User::class.java)
            members.add(newMember)
            membersAdapter.submitList(members.toList())
        }


    }

//    Picasso.get().load("$SERVER_IP/doyo/images/icons/${body.get("icon")}").into(binding.imageView3)

//        val a = GradientDrawable()
//        a.setStr

//            val mes = "{\"username\":\"${AccountService.username}\",\"value\":\"${binding.chatEdit.text}\"}"
}