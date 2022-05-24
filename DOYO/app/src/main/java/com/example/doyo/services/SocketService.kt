package com.example.doyo.services

import com.example.doyo.SERVER_IP
import io.socket.client.IO
import io.socket.client.Socket
import okhttp3.OkHttpClient
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit

object SocketService {

    lateinit var socket: Socket

    @Synchronized
    fun initSocket(token: String) {
        try {
//          "http://10.0.2.2:3000" is the network your Android emulator must use to join the localhost network on your computer
//          "http://localhost:3000/" will not work
//          If you want to use your physical phone you could use your ip address plus :3000
//          This will allow your Android Emulator and physical device at your home to connect to the server
            val options: IO.Options = IO.Options()
            options.path = "/socket/socket.io"
            options.query = "token=$token"
//            options.forceNew = true;
//            options.reconnection = true;
//            options.upgrade = false
            val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
            options.callFactory = clientBuilder.build()
            println("vvvvv")
            socket = IO.socket(SERVER_IP, options)
        } catch (e: URISyntaxException) {

        }
    }


}