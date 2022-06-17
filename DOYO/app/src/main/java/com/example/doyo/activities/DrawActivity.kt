package com.example.doyo.activities

import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import com.example.doyo.PaintView
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.doyo.R
import com.example.doyo.databinding.ActivityDrawBinding
import com.example.doyo.services.SocketService
import com.example.doyo.toBitmap
import org.json.JSONObject
import com.example.doyo.toBase64
import com.example.doyo.fragments.ClearDialog
import com.example.doyo.fragments.ColorPicker


class DrawActivity : AppCompatActivity(), ColorPicker.ColorPickerListener {
    private lateinit var drawBinding: ActivityDrawBinding

    private val socket = SocketService.socket
    private var stage = 0

    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawBinding = ActivityDrawBinding.inflate(layoutInflater)
        setContentView(drawBinding.root)

        drawBinding.paintView.init(Color.TRANSPARENT)

        val clickAnim = AnimationUtils.loadAnimation(this, R.anim.anim_draw_item)

        val time = intent.getLongExtra("time", 0)
        println("TIME: $time")
        val frames = intent.getIntExtra("frames", 0)
        println("FRAMES: $frames")

        timer = object: CountDownTimer(time*1000, 1000) {
            override fun onTick(time: Long) {
                val minutes = time / 1000 / 60
                val seconds = time / 1000 % 60
                drawBinding.paintHeader.textTimer.text = if (seconds < 10) "$minutes:0$seconds" else "$minutes:$seconds"
            }
            override fun onFinish() {
                drawBinding.paintHeader.textTimer.text = "0:00"
                sendFrame()
            }
        }

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
            sendFrame()
            drawBinding.paintHeader.btnFinish.isClickable = false
            drawBinding.paintHeader.btnFinish.textSize = 12f
            drawBinding.paintHeader.btnFinish.text = "Waiting for others"
        }

        startTimer()
        drawBinding.paintHeader.stage.text = "${stage+1}/$frames"

        socket.on("next") { data ->
            Handler(Looper.getMainLooper()).post {
                cancelTimer()
                drawBinding.paintView.clear() //очистить поле для рисования, важно делать это ПОСЛЕ получения картинки с белым фоном
                drawBinding.backgroundImage.setImageBitmap(toBitmap(data[0].toString()))
                drawBinding.backgroundImage.visibility = View.VISIBLE
                startTimer()
                stage++
                drawBinding.paintHeader.btnFinish.isClickable = true
                drawBinding.paintHeader.btnFinish.text = "FINISH"
                drawBinding.paintHeader.stage.text = "${stage+1}/$frames"
            }
        }

        socket.on("finish") {
            Handler(Looper.getMainLooper()).post {
                stage = 0
                finish()
            }
        }

//        drawBinding.backgroundImage.setImageBitmap(toBitmap("STRING BASE64 FROM SERVER"))
//        drawBinding.backgroundImage.visibility = View.VISIBLE
    }

    override fun onDialogResult(color: Int) {
        PaintView.paint.color  = color
        drawBinding.paintFooter.btnColorPicked.background.setTint(PaintView.paint.color)
    }

    private fun startTimer() {
        timer.start()
    }

    private fun cancelTimer() {
        timer.cancel()
    }

    private fun sendFrame() {
        val bitmap = drawBinding.paintView.whiteBackgroundBitmap()
        val encoded = toBase64(bitmap) //это передается на сервер
        //drawBinding.paintView.clear() //очистить поле для рисования, важно делать это ПОСЛЕ получения картинки с белым фоном
        val data = JSONObject()
        data.put("stage", stage)
        data.put("image", encoded)
        socket.emit("next", data)
    }

    override fun onDestroy() {
        super.onDestroy()
        socket.off("next")
        socket.off("finish")
    }
}
