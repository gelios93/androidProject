package com.example.doyo.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.doyo.R
import com.example.doyo.adapters.ColorListAdapter
import com.example.doyo.databinding.ColorPickerFragmentBinding

import java.lang.ClassCastException

class ColorPicker: DialogFragment() {

    private lateinit var listener: ColorPickerListener

    interface ColorPickerListener {
        fun onDialogResult(color: Int)
    }

    private lateinit var binding: ColorPickerFragmentBinding
    private var currentColor: Int = Color.BLACK

    companion object {
        fun newInstance(color: Int): ColorPicker {
            val args = Bundle()
            args.putInt("color", color)
            val fragment = ColorPicker()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentColor = arguments?.getInt("color") as Int
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext())
        binding = ColorPickerFragmentBinding.inflate(layoutInflater)
        val colors: Array<Int> = resources.getIntArray(R.array.palette).toList().toTypedArray()
        binding.colorList.adapter = ColorListAdapter(
            colors,
            layoutInflater,
            colors.indexOf(currentColor),
            object : ColorListAdapter.OnItemClickListener {
                override fun onClickColor(position: Int) {
                    currentColor = colors[position]
                }
            },
            requireContext())
        binding.colorList.layoutManager = GridLayoutManager(requireContext(), 6, GridLayoutManager.VERTICAL, false)
        dialog
            .setView(binding.root)
            .setTitle("Choose the color")
            .setPositiveButton("OK") { dlg, _ ->
                dlg.dismiss()
            }
        return dialog.show()
    }

    override fun onDetach() {
        super.onDetach()
        listener.onDialogResult(currentColor)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ColorPickerListener
        } catch (e: ClassCastException) {
            println(e.printStackTrace())
        }
    }
}
