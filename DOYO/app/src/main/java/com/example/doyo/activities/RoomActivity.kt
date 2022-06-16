package com.example.doyo.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.doyo.DELETE_ROOM
import com.example.doyo.FRIEND_REQUEST
import com.example.doyo.LEAVE_ROOM
import com.example.doyo.DELETE_PLAYER
import com.example.doyo.R
import com.example.doyo.TopSheetBehavior
import com.example.doyo.adapters.MemberListLongAdapter
import com.example.doyo.adapters.MemberListShortAdapter
import com.example.doyo.adapters.MessageListAdapter
import com.example.doyo.databinding.RoomCoordinatorBinding
import com.example.doyo.fragments.ConfirmationDialog
import com.example.doyo.fragments.CountDownDialog
import com.example.doyo.fragments.EditRoomDialog
import com.example.doyo.fragments.InvitationDialog
import com.example.doyo.isHost
import com.example.doyo.models.Message
import com.example.doyo.models.User
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.Gson
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*


class RoomActivity : AppCompatActivity() {

    private val socket: Socket = SocketService.socket
    private val messages: MutableList<Message> = mutableListOf()

    private lateinit var members: MutableList<User>
    private lateinit var colors: MutableList<Int>
    private fun fillColors(){
        colors =
            mutableListOf(
                ContextCompat.getColor(this, R.color.pink_palette),
                ContextCompat.getColor(this, R.color.sky_blue_palette),
                ContextCompat.getColor(this, R.color.green_palette),
                ContextCompat.getColor(this, R.color.cyan_palette),
                ContextCompat.getColor(this, R.color.yellow_palette),
                ContextCompat.getColor(this, R.color.orange_palette)
            )
    }

    private lateinit var membersListShort: RecyclerView
    private lateinit var membersListLong: RecyclerView

    private lateinit var chatList: RecyclerView
    private lateinit var chatEdit: EditText
    private lateinit var sendButton: Button

    private lateinit var quitButton: ImageButton
    private lateinit var editButton: Button
    private lateinit var searchButton: Button
    private lateinit var addButton: Button
    private lateinit var loadingBar: ProgressBar
    private lateinit var startButton: Button
    private lateinit var arrowButton: ImageButton

    private lateinit var playersText: TextView
    private lateinit var timeText: TextView
    private lateinit var framesText: TextView

    private var isSearching = false
    private var players = 0
    private var time = 0
    private var frames = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fillColors()

        val respBody = JSONObject(intent.getStringExtra("data")!!)
        println(respBody.toString(2))

        val binding = RoomCoordinatorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        membersListShort = binding.room.members
        membersListLong = binding.settings.members

        chatList = binding.room.chat
        chatEdit = binding.room.chatEdit
        sendButton = binding.room.chatSend

        quitButton = binding.settings.quitButton
        editButton = binding.settings.editButton
        searchButton = binding.settings.searchButton
        addButton = binding.settings.addButton
        loadingBar = binding.settings.progressBar
        startButton = binding.settings.startButton
        arrowButton = binding.settings.arrowButton

        playersText = binding.settings.playersValue
        timeText = binding.settings.timeValue
        framesText = binding.settings.framesValue

        val topBehavior = TopSheetBehavior.from(binding.settings.root)
        topBehavior.setTopSheetCallback(object : TopSheetBehavior.TopSheetCallback() {

            var isExpanded: Boolean = false

            override fun onStateChanged(topSheet: View, newState: Int) {

                if (newState == TopSheetBehavior.STATE_EXPANDED)
                    isExpanded = true
                if (newState == TopSheetBehavior.STATE_COLLAPSED)
                    isExpanded = false

            }
            override fun onSlide(topSheet: View, slideOffset: Float) {

                if (isExpanded)
                    arrowButton.rotation = (1f - slideOffset) * 180 + 180
                else
                    arrowButton.rotation = slideOffset * 180

            }
        })
        arrowButton.setOnClickListener {
            if (topBehavior.state == TopSheetBehavior.STATE_COLLAPSED)
                topBehavior.state = TopSheetBehavior.STATE_EXPANDED
            else
                topBehavior.state = TopSheetBehavior.STATE_COLLAPSED
        }



        members =
            if (respBody.has("players")) {
                isHost = false

                val mem = Gson().fromJson(respBody.getString("players"), Array<User>::class.java).toMutableList()
                for (m in mem){
                    m.color = extractColor()
                }

                players = respBody.getJSONObject("settings").getInt("players_num")
                time = respBody.getJSONObject("settings").getInt("game_time") / 1000 //milliseconds to seconds
                frames = respBody.getJSONObject("settings").getInt("frames_num")

                mem
            }
            else {
                isHost = true

                players = respBody.getInt("players_num")
                time = respBody.getInt("game_time") / 1000 //milliseconds to seconds
                frames = respBody.getInt("frames_num")

                mutableListOf(
                    User(
                        AccountService.username,
                        " ",
                        AccountService.experience,
                        extractColor()
                    )
                )
            }

        updateTextViews()

        if (isHost) {

            loadingBar.visibility = View.INVISIBLE

            editButton.setOnClickListener {
                val dialog = EditRoomDialog(members.size, players, time, frames)
                dialog.show(supportFragmentManager, "editDialog")
            }

            searchButton.setOnClickListener {
                if (isSearching){
                    socket.emit("stopSearch")
                    searchButton.text = "SEARCH"
                    loadingBar.visibility = View.INVISIBLE
                    isSearching = false
                }
                else {
                    socket.emit("search")
                    searchButton.text = "STOP"
                    loadingBar.visibility = View.VISIBLE
                    isSearching = true
                }
            }

            startButton.setOnClickListener {
                socket.emit("start")
            }

            quitButton.setOnClickListener {
                val dialog = ConfirmationDialog(DELETE_ROOM)
                dialog.show(supportFragmentManager, "deleteRoomDialog")
            }



        }
        else {
            editButton.visibility = View.GONE

            searchButton.isClickable = false

            if (respBody.getBoolean("state")){
                searchButton.text = "SEARCHING..."
                loadingBar.visibility = View.VISIBLE
                isSearching = true
            }
            else {
                searchButton.text = "NOT SEARCHING"
                loadingBar.visibility = View.INVISIBLE
                isSearching = false
            }


            val param = startButton.layoutParams as ViewGroup.MarginLayoutParams
            param.marginEnd = -convertDpToPx(70)
            startButton.layoutParams = param
            startButton.visibility = View.INVISIBLE


            socket.on("search") {
                Handler(Looper.getMainLooper()).post {
                    searchButton.text = "SEARCHING..."
                    loadingBar.visibility = View.VISIBLE
                }
                isSearching = true
            }

            socket.on("stopSearch") {
                Handler(Looper.getMainLooper()).post {
                    searchButton.text = "NOT SEARCHING"
                    loadingBar.visibility = View.INVISIBLE
                }
                isSearching = false
            }

            quitButton.setOnClickListener {
                val dialog = ConfirmationDialog(LEAVE_ROOM)
                dialog.show(supportFragmentManager, "leaveRoomDialog")
            }
        }


        addButton.setOnClickListener {
            val dialog = InvitationDialog()
            dialog.show(supportFragmentManager, "invitationDialog")
        }


        val membersShortAdapter = MemberListShortAdapter(layoutInflater)
        membersListShort.adapter = membersShortAdapter
        membersListShort.setHasFixedSize(true)
        membersListShort.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        membersShortAdapter.submitList(members.toList())

        val membersLongAdapter = MemberListLongAdapter(layoutInflater, supportFragmentManager)
        membersListLong.adapter = membersLongAdapter
        membersListShort.setHasFixedSize(true)
        membersListLong.layoutManager = LinearLayoutManager(this)
        membersLongAdapter.submitList(members.toList())

        socket.on("friendRequestResponse") { arg ->
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, arg[0].toString(), Toast.LENGTH_SHORT).show()
            }
        }

        socket.on("deleteRoom") {
            println("deleteRoom")

            Handler(Looper.getMainLooper()).post {
                socket.emit("leaveRoom")
                Toast.makeText(this, "Room has been deleted", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        socket.on("leave") { arg ->
            if (arg.isEmpty()) {
                socket.emit("leaveRoom")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "You have left the room", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else {
                println("leave")
                val username = arg[0].toString()
                members.remove(members.find { user -> user.username == username })
                membersLongAdapter.submitList(members.toList())
                membersShortAdapter.submitList(members.toList())
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "$username has left the room!", Toast.LENGTH_SHORT).show()
                }
                updateTextViews()
            }
        }

        socket.on("deleteUser") { arg ->
            if (arg.isEmpty()) {
                socket.emit("leaveRoom")
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "You have been kicked by host", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            else {
                val username = arg[0].toString()
                members.remove(members.find { user -> user.username == username })
                membersLongAdapter.submitList(members.toList())
                membersShortAdapter.submitList(members.toList())
                Handler(Looper.getMainLooper()).post {
                    Toast.makeText(this, "$username has been kicked by host", Toast.LENGTH_SHORT).show()
                }
                updateTextViews()
            }
        }


        socket.on("add") { arg ->
            val newMember = Gson().fromJson(arg[0].toString(), User::class.java)
            newMember.color = extractColor()
            members.add(newMember)
            println("members:")
            println(members.toList())
            membersListShort.setHasFixedSize(false)
            membersShortAdapter.submitList(members.toList())
            membersListShort.setHasFixedSize(true)
            membersLongAdapter.submitList(members.toList())

            Handler(Looper.getMainLooper()).post {
                Toast.makeText(this, "${newMember.username} has joined!", Toast.LENGTH_SHORT).show()
            }

            updateTextViews()
        }



        val messagesAdapter = MessageListAdapter(layoutInflater, messages)
        chatList.adapter = messagesAdapter
        chatList.setHasFixedSize(true)
        val manager = LinearLayoutManager(this)
        manager.stackFromEnd = true
        chatList.layoutManager = manager

        sendButton.setOnClickListener {
            if (chatEdit.text.toString() != ""){
                val mes = JSONObject()
                mes.put("username", AccountService.username)
                mes.put("value", chatEdit.text.toString())

                socket.emit("message", mes.toString())
                chatEdit.setText("")
            }
        }

        socket.on("message") { arg ->
            println("MESSAGE")
            val newMessage = JSONObject(arg[0].toString())
            val date = Date()
            val minutes = if (date.minutes < 10) "0${date.minutes}" else date.minutes
            val hours = date.hours
            messages.add(Message(newMessage.getString("value"), members.find { user ->
                user.username == newMessage.getString(
                    "username"
                )
            }!!, "$hours:$minutes"))
            Handler(Looper.getMainLooper()).post {
                chatList.scrollToPosition(messages.size - 1)
                messagesAdapter.notifyItemInserted(messages.size)
            }

        }

        socket.on("settings") { arg ->
            val newSettings = JSONObject(arg[0].toString())
            players = newSettings.getInt("players_num")
            time = newSettings.getInt("game_time") / 1000
            frames = newSettings.getInt("frames_num")
            updateTextViews()
        }

        socket.on("start") {
            println("start")
            val dialog = CountDownDialog(time)
            Handler(Looper.getMainLooper()).post {
                dialog.show(supportFragmentManager, "countDialog")
            }
        }
    }

    override fun onBackPressed() {
        if (isHost) {
            val dialog = ConfirmationDialog(DELETE_ROOM)
            dialog.show(supportFragmentManager, "deleteRoomDialog")
        }
        else{
            val dialog = ConfirmationDialog(LEAVE_ROOM)
            dialog.show(supportFragmentManager, "leaveRoomDialog")
        }
    }

    private fun extractColor(): Int {
        val color = colors.random()
        colors.remove(color)
        return color
    }

    private fun convertDpToPx(dps: Int): Int {
        val scale = this.resources.displayMetrics.density
        return (dps * scale + 0.5f).toInt()
    }

    private fun updateTextViews(){
        Handler(Looper.getMainLooper()).post {
            playersText.text = "${members.size}/${players}"
            timeText.text = time.toString()
            framesText.text = frames.toString()
        }
    }


//        addButton.setBackgroundColor(ContextCompat.getColor(this, R.color.red_palette))
//        addButton.setOnClickListener {
//            members.removeAt(members.size-1)
//            deleteButton.animate()
//                .translationX(50f)
//                .translationY(100f)
//                .setDuration(1000)
//                .setInterpolator(AccelerateDecelerateInterpolator())
//                .scaleX(1.5f)
//                .scaleY(1.5f)
//                .start()
//        }

//            println(HttpService.searchUsers(this, "a").toString(2))

    //searchButton.setBackgroundColor(ContextCompat.getColor(this, R.color.green_palette))

//        loadingBar.visibility = View.INVISIBLE

//        val cl = binding.settings.root
//        val cs = ConstraintSet()
//        cs.clone(cl);
//        cs.connect(chatEdit.id, cl.right, startButton.id, cl.right)
//        cs.applyTo(cl)

//        val scale = this.resources.displayMetrics.density
//        val param = startButton.layoutParams as ViewGroup.MarginLayoutParams
//        param.marginEnd = -(70 * scale + 0.5f).toInt()
//        startButton.layoutParams = param
//        startButton.visibility = View.INVISIBLE



//    membersLongAdapter.listener = object: MemberListLongAdapter.OnItemClickListener {
//        override fun onDeleteClick(position: Int) {
//            members.removeAt(position)
//            membersLongAdapter.submitList(members.toList())
//            membersShortAdapter.submitList(members.toList())
//        }
//    }

    //    Picasso.get().load("$SERVER_IP/doyo/images/icons/${body.get("icon")}").into(binding.imageView3)

//        val a = GradientDrawable()
//        a.setStr

//            val mes = "{\"username\":\"${AccountService.username}\",\"value\":\"${binding.chatEdit.text}\"}"
}