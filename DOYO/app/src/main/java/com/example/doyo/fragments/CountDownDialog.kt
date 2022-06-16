package com.example.doyo.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.doyo.activities.DrawActivity
import com.example.doyo.activities.ProfileActivity
import com.example.doyo.activities.RoomActivity
import com.example.doyo.databinding.DialogCountDownBinding
import com.example.doyo.services.SocketService

class CountDownDialog(private val time: Int): DialogFragment() {

    private lateinit var countingNumber: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val binding = DialogCountDownBinding.inflate(layoutInflater)

            countingNumber = binding.countingNumber

            object: CountDownTimer(6000, 1000){
                override fun onTick(millisUntilFinished: Long) {
                    println(millisUntilFinished / 1000)
                    countingNumber.text = (millisUntilFinished / 1000).toString()
                }

                override fun onFinish() {
                    dialog?.dismiss()
                    val intent = Intent(context, DrawActivity::class.java).apply {
                        putExtra("time", time.toLong())
                    }
                    startActivity(intent)
//                    (context as RoomActivity).finish()
                }

            }.start()

            val dial = AlertDialog.Builder(it)
                .setView(binding.root)
                .create()
            dial.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dial

        } ?: throw IllegalStateException("Activity cannot be null")
    }
}