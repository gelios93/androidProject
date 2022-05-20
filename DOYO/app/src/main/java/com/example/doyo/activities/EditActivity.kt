package com.example.doyo.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import com.example.doyo.R
import com.example.doyo.views.PaintView
import com.example.drawingapp.ClearDialog
import com.example.drawingapp.ColorPicker
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.doyo.databinding.ActivityEditBinding

lateinit var editBinding: ActivityEditBinding

class EditActivity: AppCompatActivity(), ColorPicker.ColorPickerListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(editBinding.root)

        val clickAnim = AnimationUtils.loadAnimation(this, R.anim.anim_draw_item)

        val inputBitmap = intent?.getParcelableExtra<Bitmap>("BitmapImage")
        //val inputBitmap = intent?.getStringExtra("BitmapImage")
        if (inputBitmap != null)
            println("We have icon here $inputBitmap")
            //editBinding.paintView.setBitmapIcon(inputBitmap)
        else
            //editBinding.paintView.initEdit(Color.WHITE)

        //Слайдер не находит
        //editBinding.paintFooter.slider.addOnChangeListener { _, value, _ ->
        //    PaintView.paint.strokeWidth = value
        //}


        editBinding.paintFooter.btnUndo.setOnClickListener {
            it.startAnimation(clickAnim)
            editBinding.paintView.undo()
        }

        editBinding.paintFooter.btnColor.setOnClickListener {
            it.startAnimation(clickAnim)
            val colorPicker = ColorPicker.newInstance(PaintView.paint.color)
            colorPicker.show(supportFragmentManager, "colorPicker")
        }

        editBinding.paintFooter.btnClear.setOnClickListener {
            it.startAnimation(clickAnim)
            val clearDialog = ClearDialog { _, _ ->  editBinding.paintView.clear() }
            clearDialog.show(supportFragmentManager, "clearDialog")
        }

        editBinding.paintHeader.btnSkip.setOnClickListener {
            it.startAnimation(clickAnim)
        }

        editBinding.paintHeader.btnSave.setOnClickListener {
            it.startAnimation(clickAnim)
            setResult(RESULT_CANCELED, Intent())
            finish()
        }
    }

    override fun onDialogResult(color: Int) {
        PaintView.paint.color  = color
        editBinding.paintFooter.btnColorPicked.background.setTint(PaintView.paint.color)
    }
}