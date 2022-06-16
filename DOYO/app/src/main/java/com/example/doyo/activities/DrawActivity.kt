package com.example.doyo.activities

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.doyo.PaintView
import com.example.doyo.R
import com.example.doyo.databinding.ActivityDrawBinding
import com.example.doyo.toBase64
import com.example.doyo.fragments.ClearDialog
import com.example.doyo.fragments.ColorPicker


class DrawActivity : AppCompatActivity(), ColorPicker.ColorPickerListener {
    lateinit var drawBinding: ActivityDrawBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawBinding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(drawBinding.root)

        drawBinding.paintView.init(Color.TRANSPARENT)

        val clickAnim = AnimationUtils.loadAnimation(this, R.anim.anim_draw_item)

        drawBinding.paintFooter.btnUndo.setOnClickListener {
            it.startAnimation(clickAnim)
            drawBinding.paintView.undo()
        }

        drawBinding.paintFooter.slider.addOnChangeListener { _, value, _ ->
            PaintView.paint.strokeWidth = value
        }

        drawBinding.paintFooter.btnColor.setOnClickListener {
            it.startAnimation(clickAnim)
            val colorPicker = ColorPicker.newInstance(PaintView.paint.color)
            colorPicker.show(supportFragmentManager, "colorPicker")
        }

        drawBinding.paintFooter.btnClear.setOnClickListener {
            it.startAnimation(clickAnim)
            val clearDialog = ClearDialog("Are you sure you want to clear the canvas?") { _, _ ->  drawBinding.paintView.clear() }
            clearDialog.show(supportFragmentManager, "clearDialog")
        }

        drawBinding.paintHeader.btnFinish.setOnClickListener {
            it.startAnimation(clickAnim)
            val bitmap = drawBinding.paintView.whiteBackgroundBitmap()
            val encoded = toBase64(bitmap)
            drawBinding.paintView.clear()
            //Copy to clipboard
            //val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            //val clip = ClipData.newPlainText("label", encoded)
            //clipboard.setPrimaryClip(clip)
        }
    }

    override fun onDialogResult(color: Int) {
        PaintView.paint.color  = color
        drawBinding.paintFooter.btnColorPicked.background.setTint(PaintView.paint.color)
    }

    private fun startTimer(seconds: Long) {
        val timeMills = seconds*1000
        object: CountDownTimer(timeMills, 1000) {
            override fun onTick(time: Long) {
                val minutes = time / 1000 / 60
                val seconds = time / 1000 % 60
                drawBinding.paintHeader.textTimer.text = if (seconds < 10) "$minutes:0$seconds" else "$minutes:$seconds"
            }
            override fun onFinish() {
                drawBinding.paintHeader.textTimer.text = "0:00"
            }
        }.start()
    }
}