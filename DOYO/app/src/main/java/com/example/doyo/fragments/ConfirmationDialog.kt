package com.example.doyo.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.doyo.*
import com.example.doyo.databinding.DialogEditRoomBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.SocketService
import org.json.JSONObject

class ConfirmationDialog(private val title: String, private val username: String = ""): DialogFragment()  {

    private val socket = SocketService.socket
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            AlertDialog.Builder(it)
                .setTitle(title)
                .setMessage(when(title){
                    FRIEND_REQUEST -> "Do you want to send request to $username?"
                    DELETE_PLAYER -> "Do you want to delete player $username from room?"
                    DELETE_ROOM -> "Do you want to delete room?"
                    LEAVE_ROOM -> "Do you want to leave room?"
                    JOIN_ROOM -> "You have been invited to the room. Do you want to join?"
                    else -> "Error"
                })
                .setPositiveButton("YES") { dialog, _ ->
                    when(title){
                        FRIEND_REQUEST -> {
                            socket.emit("friendRequest", username)
                        }
                        DELETE_PLAYER -> {
                            socket.emit("deleteUser", username)
                        }
                        DELETE_ROOM -> {
                            socket.emit("deleteRoom")
                        }
                        LEAVE_ROOM -> {
                            socket.emit("leave", AccountService.username)
                        }
                        JOIN_ROOM -> {
                            socket.emit("joinRoom", username)
                            socket.emit("friendInviteResponse", "yes")
                        }
                    }

                    dialog?.dismiss()
                }
                .setNegativeButton("NO") {dialog, _ ->
                    if (title == JOIN_ROOM)
                        socket.emit("friendInviteResponse", "no")
                    dialog?.dismiss()
                }
                .create()

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
