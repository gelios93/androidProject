package com.example.doyo

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat.getSystemService
import kotlin.math.abs


class PaintView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    init {
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeCap = Paint.Cap.ROUND //make the end of line round
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeWidth = 16f
        paint.isAntiAlias = true //make brush smooth
    }

    companion object {
        lateinit var bitmap: Bitmap
        const val touchTolerance = 4f
        lateinit var canvasBitmap: Canvas
        var eraseColor = Color.TRANSPARENT
        var backgroundImage: Bitmap? = null
        var paintBitmap = Paint()//pencil used to draw on Bitmap
        var pathList = ArrayList<Pair<Path, Paint>>()
        var paint = Paint() //appearance of line
        var path = Path() //appearance of line
        var xValue = 0f
        var yValue = 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (backgroundImage != null) {
            backgroundImage = Bitmap.createScaledBitmap(backgroundImage!!, width, height, false);
            bitmap = backgroundImage!!.copy(Bitmap.Config.ARGB_8888, true);
        }
        else {
            //create a bucket where we can put all the pixels which we trace around the screen
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888) //has rgb channel without transparency
            bitmap.eraseColor(eraseColor) //fill with color
        }
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
        bitmap.eraseColor(eraseColor)
        backgroundImage = null
        invalidate()
    }

    fun undo() {
        if (pathList.size >= 1) {
            pathList.removeAt(pathList.size - 1)
            if (backgroundImage != null)
                bitmap = backgroundImage!!.copy(Bitmap.Config.ARGB_8888, true)
            else {
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                bitmap.eraseColor(eraseColor)
            }
            canvasBitmap = Canvas(bitmap)
            for ((first, second) in pathList)
                canvasBitmap.drawPath(first, second)
            invalidate()
        }
    }

    fun init(color: Int, icon: Bitmap? = null) {
        eraseColor = color
        paint.color = Color.BLACK
        paint.strokeWidth = 16f
        pathList.clear()
        path = Path()
        xValue = 0f
        yValue = 0f
        backgroundImage = icon
        invalidate()
    }

    fun whiteBackgroundBitmap(): Bitmap {
        val newBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val newCanvas = Canvas(newBitmap)
        newCanvas.drawColor(Color.YELLOW)
        newCanvas.drawBitmap(bitmap, 0f, 0f, null)
        return newBitmap
    }
}