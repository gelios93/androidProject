package com.example.doyo.services

import android.app.Service
import android.content.Intent

import android.os.IBinder
import android.util.Log
import androidx.annotation.Nullable


class ClosingService : Service() {
    @Nullable
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        SocketService.socket.disconnect()
        Log.d("socket", "Socket disconnected!")

        super.onTaskRemoved(rootIntent)
        // Destroy the service
        stopSelf()
    }
}