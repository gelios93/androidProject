package com.example.doyo.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Typeface
import android.os.Bundle
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.doyo.R
import com.example.doyo.databinding.DialogEditRoomBinding
import com.example.doyo.services.SocketService
import org.json.JSONObject

class EditRoomDialog (private val currentPlayers: Int, private val players: Int, private val time: Int, private val frames: Int): DialogFragment() {

    private lateinit var playersPicker: NumberPicker
    private lateinit var timePicker: NumberPicker
    private lateinit var framesPicker: NumberPicker

    private val playersVariants = arrayOf("2", "3", "4", "5", "6", "7")
    private val timeVariants = arrayOf("20", "30", "40", "50", "60", "70", "80")
    private val framesVariants = arrayOf("3", "4", "5", "6", "7", "8", "9")


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val binding = DialogEditRoomBinding.inflate(layoutInflater)

            playersPicker = binding.playersPicker
            timePicker = binding.timePicker
            framesPicker = binding.framesPicker

            val newPlayersVariants = playersVariants.copyOfRange(if (currentPlayers == 1) 0 else playersVariants.indexOf(currentPlayers.toString()), playersVariants.size-1)
            playersPicker.minValue = 0
            playersPicker.maxValue = newPlayersVariants.size-1
            playersPicker.displayedValues = newPlayersVariants
            playersPicker.wrapSelectorWheel = false
            playersPicker.value = newPlayersVariants.indexOf(players.toString())

            timePicker.minValue = 0
            timePicker.maxValue = timeVariants.size-1
            timePicker.displayedValues = timeVariants
            timePicker.wrapSelectorWheel = false
            timePicker.value = timeVariants.indexOf(time.toString())

            framesPicker.minValue = 0
            framesPicker.maxValue = framesVariants.size-1
            framesPicker.displayedValues = framesVariants
            framesPicker.wrapSelectorWheel = false
            framesPicker.value = framesVariants.indexOf(frames.toString())

            val al = AlertDialog.Builder(it)
                .setView(binding.root)
                .setPositiveButton("Ok") { dialog, _ ->
                    val request = JSONObject()
                    request.put("players_num", newPlayersVariants[playersPicker.value].toInt())
                    request.put("game_time", timeVariants[timePicker.value].toInt()*1000)
                    request.put("frames_num", framesVariants[framesPicker.value].toInt())

                    SocketService.socket.emit("settings", request)

                    println(newPlayersVariants[playersPicker.value])
                    println(timeVariants[timePicker.value])
                    println(framesVariants[framesPicker.value])
                    dialog?.dismiss()
                }
                .setNegativeButton("Cancel", null)
                .create()

            al

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}