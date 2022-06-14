package com.example.doyo.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.DialogFragment
import com.example.doyo.databinding.DialogEditBinding
import com.example.doyo.isValidName

class EditNameDialog(private val username: String, private var listener: SaveNameListener): DialogFragment() {

    interface SaveNameListener {
        fun saveName(context: Context, input: String): String?
    }

    lateinit var binding: DialogEditBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        binding = DialogEditBinding.inflate(layoutInflater)
        binding.username.text = Editable.Factory.getInstance().newEditable(username)
        binding.username.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) binding.editUsername.error = null
        }

        builder.setTitle("Edit your name")
            .setView(binding.root)
            .setNegativeButton("CANCEL", null)
            .setPositiveButton("SAVE", null)
        return builder.show()
    }

    //Handle dismiss call of positive button
    override fun onResume() {
        super.onResume()
        val d = dialog as AlertDialog
        d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val input = binding.username.text.toString()
            if (input == username)
                d.dismiss()
            else {
                val validation = isValidName(input)
                if (!validation.result) {
                    binding.editUsername.error = validation.error
                    binding.username.clearFocus()
                }
                else {
                    val result = listener.saveName(requireContext(), binding.username.text.toString())
                    if (result == null)
                        d.dismiss()
                    else {
                        binding.editUsername.error = result
                        binding.username.clearFocus()
                    }
                }
            }
        }
    }
}