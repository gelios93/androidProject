package com.example.drawingapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ClearDialog(private val listener: DialogInterface.OnClickListener): DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle("Are you sure you want to clear the canvas?")
            .setPositiveButton("YES", listener)
            .setNegativeButton("NO", null)
            .show()
    }
}