package com.example.doyo.activities

import android.graphics.Color
import android.os.Bundle
import com.example.doyo.R
import com.example.doyo.PaintView
import com.example.drawingapp.ClearDialog
import com.example.drawingapp.ColorPicker
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.doyo.databinding.ActivityEditBinding
import com.example.doyo.services.AccountService
import com.example.doyo.services.HttpService
import com.example.doyo.toBase64

class EditActivity: AppCompatActivity(), ColorPicker.ColorPickerListener {

    lateinit var editBinding: ActivityEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editBinding = ActivityEditBinding.inflate(layoutInflater)
        setContentView(editBinding.root)

        val clickAnim = AnimationUtils.loadAnimation(this, R.anim.anim_draw_item)

        if (intent?.extras?.get("mode") == "new")
            editBinding.paintView.init(Color.WHITE)
        if (intent?.extras?.get("mode") == "edit")
            editBinding.paintView.init(Color.WHITE, icon = AccountService.icon)


        editBinding.paintFooter.slider.addOnChangeListener { _, value, _ ->
            PaintView.paint.strokeWidth = value
        }

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
            setResult(RESULT_CANCELED)
            finish()
        }

        editBinding.paintHeader.btnSave.setOnClickListener {
            it.startAnimation(clickAnim)
            val response = HttpService.editData(this, icon = toBase64(PaintView.bitmap))
            if (response.get("message") == "Success") {
                AccountService.icon = PaintView.bitmap
                setResult(RESULT_OK)
            }
            else {
                setResult(RESULT_CANCELED)
                Toast.makeText(this, "Saving error", Toast.LENGTH_SHORT).show()
            }
            finish()
        }
    }

    override fun onDialogResult(color: Int) {
        PaintView.paint.color  = color
        editBinding.paintFooter.btnColorPicked.background.setTint(PaintView.paint.color)
    }
}