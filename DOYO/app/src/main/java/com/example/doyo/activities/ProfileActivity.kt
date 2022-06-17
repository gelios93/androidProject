package com.example.doyo.activities

import android.app.Instrumentation
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.example.doyo.DELETE_ROOM
import com.example.doyo.JOIN_ROOM
import androidx.viewpager2.widget.ViewPager2
import com.example.doyo.R
import com.example.doyo.adapters.TabFragmentAdapter
import com.example.doyo.contracts.EditContract
import com.example.doyo.contracts.ProfileContract
import com.example.doyo.databinding.ActivityProfileBinding
import com.example.doyo.fragments.ConfirmationDialog
import com.example.doyo.fragments.EditNameDialog
import com.example.doyo.models.User
import com.example.doyo.fragments.FriendsFragment
import com.example.doyo.fragments.GalleryFragment
import com.example.doyo.fragments.SearchFragment
import com.example.doyo.services.AccountService
import com.example.doyo.services.ClosingService
import com.example.doyo.services.HttpService
import com.example.doyo.services.SocketService
import com.google.gson.Gson
import com.google.android.material.tabs.TabLayoutMediator
import io.socket.client.Socket
import org.json.JSONObject


class ProfileActivity : AppCompatActivity() {

    private var indicatorWidth = 0
    private lateinit var socket: Socket
    private val list = listOf(GalleryFragment.newInstance(AccountService.animations), FriendsFragment.newInstance(), SearchFragment.newInstance())
    private lateinit var editLauncher: ActivityResultLauncher<EditContract.Input>
    private val titles = listOf("GALLERY", "FRIENDS", "SEARCH")
    private var isResume = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Less.

//        if(intent.hasExtra("data"))
//            val respBody = JSONObject(intent.getStringExtra("data")!!)
//        else

//        println(respBody.toString(2))

        //AccountService.initAccount(respBody)
        //SocketService.initSocket(respBody.getString("accessToken"))

        socket = SocketService.socket
//        socket.connect()
        socket.off()
        socket.on("connect") {
            println(socket.id())
            socket.on("hello") { args ->
                println(args[0])
            }
            socket.emit("hello", "Боже..........................")
        }
        socket.on("init") { body ->
            val intent = Intent(this, RoomActivity::class.java).apply {
                putExtra("data", body[0].toString())
            }
            startActivity(intent)
            finish()
        }
        socket.on("friendInvite") { body ->
            println("friendInvite")
            val data = JSONObject(body[0].toString())
            println(data.toString(2))
            if (isResume) {
                val dialog = ConfirmationDialog(JOIN_ROOM, data.getString("username"), data.getString("room"))
                Handler(Looper.getMainLooper()).post {
                    dialog.show(supportFragmentManager, "joinRoomDialog")
                }
            }
        }

        val closingService = Intent(this, ClosingService::class.java)
        startService(closingService)

        val binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        editLauncher = registerForActivityResult(EditContract()) {
            when(it.result) {
                true -> {
                    binding.icon.setImageBitmap(AccountService.icon)
                }
                false -> {
                    if (AccountService.icon != null)
                        binding.icon.setImageBitmap(AccountService.icon)
                    else {
                        val drawable = VectorDrawableCompat.create(resources, R.drawable.basic_icon, this.theme)
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

        val clickAnim = AnimationUtils.loadAnimation(this, R.anim.anim_draw_item)

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

        binding.icon.setOnClickListener {
            editLauncher.launch(EditContract.Input("edit"))
        }

        binding.editButton.setOnClickListener {
            it.startAnimation(clickAnim)
            val editDialog = EditNameDialog (AccountService.username, object: EditNameDialog.SaveNameListener {
                override fun saveName(context: Context, input: String): String? {
                    val response = HttpService.editData(context, username = input)
                    println(response.toString(2))
                    return when {
                        response.get("message") == "Success" -> {
                            AccountService.username = input
                            binding.username.text = input
                            null
                        }
                        response.get("code") == 406 -> "This username is already in use"
                        else -> {
                            Toast.makeText(context, response.get("message").toString(), Toast.LENGTH_SHORT).show()
                            null
                        }
                    }
                }
            })
            editDialog.show(supportFragmentManager, "editNameDialog")
        }

        val adapter = TabFragmentAdapter(this, list)
        binding.viewPager2.adapter = adapter
        TabLayoutMediator(binding.tab, binding.viewPager2) { tab, pos ->
            tab.text = when (pos) {
                0 -> titles[pos]
                1 -> titles[pos]
                2 -> titles[pos]
                else -> ""
            }
        }.attach()

        binding.tab.post{
            indicatorWidth = binding.tab.width/3
            val indicatorParams = binding.indicator.layoutParams
            indicatorParams.width = indicatorWidth
            binding.indicator.layoutParams = indicatorParams
        }

        binding.viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                val params = binding.indicator.layoutParams as FrameLayout.LayoutParams
                val translationOffset = (positionOffset + position) * indicatorWidth
                params.leftMargin = translationOffset.toInt()
                binding.indicator.layoutParams = params
            }
        })
    }

    override fun onResume() {
        super.onResume()
        isResume = true
    }

    override fun onPause() {
        super.onPause()
        isResume = false
    }

    override fun onBackPressed() {
        super.onBackPressed()
        socket.disconnect()
        Log.d("socket", "Socket disconnected!")
    }
}
