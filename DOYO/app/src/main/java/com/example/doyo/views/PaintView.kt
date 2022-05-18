package com.example.doyo.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Base64
import android.view.MotionEvent
import android.view.View
import java.io.ByteArrayOutputStream
import kotlin.math.abs


class PaintView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND //make the and of line round
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = 40f
        paint.isAntiAlias = true //make brush or texture smooth
    }

    companion object {
        lateinit var bitmap: Bitmap
        lateinit var canvasBitmap: Canvas
        const val touchTolerance = 4f
        var paintBitmap = Paint()//pencil used to draw on Bitmap
        var pathList = ArrayList<Pair<Path, Paint>>()
        var paint = Paint() //appearance of line
        var path = Path() //appearance of line
        var xValue = 0f
        var yValue = 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //create a bucket where we can put all the pixels which we trace around the screen
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) //has rgb channel without transparency
        canvasBitmap = Canvas(bitmap) //put bitmap which will be drawn by Canvas
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap, 0f, 0f, paintBitmap) //starts drawing from 0; 0 coordinates
        if (pathList.isNotEmpty())
            canvas.drawPath(pathList.last().first, pathList.last().second);
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStarted(event.x, event.y)
            }
            MotionEvent.ACTION_UP -> {
                touchEnded()
            }
            MotionEvent.ACTION_MOVE -> {
                touchMoved(event.x, event.y)
            }
            else -> return true
        }
        invalidate() //redraw the screen
        return true

    }

    private fun touchStarted(x: Float, y: Float) {
        val newPaint = Paint(paint)
        //val newPath = Path(path)
        pathList.add(Pair(path, newPaint));
        //move to the coordinates of the touch
        path.reset()
        path.moveTo(x,y)
        xValue = x
        yValue = y
    }

    private fun touchMoved(x: Float, y: Float) {
        //Calculate how far user moved from the last update
        val deltaX = abs(x - xValue)
        val deltaY = abs(y - yValue)

        if (deltaX >= touchTolerance && deltaY >= touchTolerance) {
            //quadTo() draws a curve from the current point to the defined point
            //Move the path to rhe new location
            path.quadTo(xValue, yValue, (x + xValue) / 2, (y + yValue) / 2)
            //Store new location
            xValue = x
            yValue = y
        }
    }

    private fun touchEnded() {
        path.lineTo(xValue, yValue)
        canvasBitmap.drawPath(path, paint)
        path = Path()
    }

    fun clear() {
        pathList.clear()
        bitmap.eraseColor(Color.TRANSPARENT)
        invalidate()
    }

    fun undo() {
        if (pathList.size >= 1) {
            pathList.removeAt(pathList.size - 1)
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            canvasBitmap = Canvas(bitmap)
            for ((first, second) in pathList)
                canvasBitmap.drawPath(first, second)
            invalidate()
        }
    }

    fun toBase64(): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray: ByteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

}