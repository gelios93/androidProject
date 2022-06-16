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
            //Ініціалізація налаштувань з'єднання
            val options: IO.Options = IO.Options()
            options.path = "/socket/socket.io"
            options.query = "token=$token"
            val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
                .connectTimeout(0, TimeUnit.MILLISECONDS)
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .writeTimeout(0, TimeUnit.MILLISECONDS)
            options.callFactory = clientBuilder.build()
            //Встановлення постійного з'єднання
            socket = IO.socket(SERVER_IP, options)
        } catch (e: URISyntaxException) { }
    }
}



