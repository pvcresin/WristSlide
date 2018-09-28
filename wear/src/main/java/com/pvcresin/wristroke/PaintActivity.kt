package com.pvcresin.wristroke

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.*
import java.lang.Math.*

class PaintActivity : WearableActivity() {
    private lateinit var wristrokeReceiver: WristokeReceiver

    internal var TAG = "PaintActivity"

    internal lateinit var mGestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAmbientEnabled()

        val surface = MySurfaceView(this)
        setContentView(surface)

        wristrokeReceiver = object : WristokeReceiver(this) {
            override fun onDown() {
                surface.makeCircle(200 - surface.cx.toInt() + 200, 200 - surface.cy.toInt() + 200)
                surface.draw()
            }

            override fun onMove(dx: Short, dy: Short) {
                surface.cx += dx
                surface.cy += dy
                if (wristrokeReceiver.pressed)
                    surface.makeCircle(200 - surface.cx.toInt() + 200, 200 - surface.cy.toInt() + 200)
                surface.draw()
            }
        }
        wristrokeReceiver.debug = true

        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                Log.d(TAG, "long tap")
                surface.initCursor()
                surface.draw()
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                Log.d(TAG, "double tap")
                finish()
                return super.onDoubleTap(e)
            }
        })

        // back light always on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return mGestureDetector.onTouchEvent(event) || super.dispatchTouchEvent(event)
    }

    override fun onResume() {
        super.onResume()
        wristrokeReceiver.connect()
    }

    override fun onPause() {
        wristrokeReceiver.disconnect()
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    class MySurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {

        val TAG = "surface 53"
        var circles = mutableListOf<Circle>()
        var cx = 200f
        var cy = 200f

        init {
            holder.addCallback(this)
        }

        override fun surfaceCreated(holder: SurfaceHolder) {
            draw()
        }

        fun Float.toCenterAxisX() = this + cx - width / 2

        fun Float.toCenterAxisY() = this + cy - height / 2


        fun draw() {
            val canvas: Canvas? = holder.lockCanvas()

            if (canvas != null) {
                Log.d(TAG, "draw 2")

                canvas.drawColor(Color.WHITE)

                val paint = Paint()
                paint.isAntiAlias = true


                // grid
                paint.color = Color.LTGRAY

                val gridStep = 50

                for (i in -width..width * 2 step gridStep) {
                    canvas.drawLine(
                            i.toFloat().toCenterAxisX(), (-height).toFloat().toCenterAxisY(),
                            i.toFloat().toCenterAxisX(), (height * 2).toFloat().toCenterAxisY(), paint)
                }
                for (j in -height..height * 2 step gridStep) {
                    canvas.drawLine(
                            (-width).toFloat().toCenterAxisX(), j.toFloat().toCenterAxisY(),
                            (width * 2).toFloat().toCenterAxisX(), j.toFloat().toCenterAxisY(), paint)
                }

                // circles
                paint.color = Color.GRAY

                val R = 150
                val r = 10f
                val degStep = 30

                for (deg in 0..360 step degStep) {
                    val radX = toRadians(deg.toDouble())
                    val radY = toRadians(deg.toDouble())

                    canvas.drawCircle(
                            (width/2 + R * cos(radX)).toFloat().toCenterAxisX(),
                            (height/2 + R * sin(radY)).toFloat().toCenterAxisY(), r, paint)
                }

                circles.forEach { it.draw(paint, canvas) }

                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                canvas.drawCircle(cx, cy, 10.0f, paint)

                paint.color = Color.BLACK
                canvas.drawLine(width/2f - 10, height/2f, width/2f + 10, height/2f, paint)
                canvas.drawLine(width/2f, height/2f - 10, width/2f, height/2f + 10, paint)

                holder.unlockCanvasAndPost(canvas)
            }
        }

        fun initCursor() {
            cx = 200f
            cy = 200f
        }

        fun makeCircle(x: Int, y: Int) {
            circles.add(Circle(x, y))
        }

        fun click() {

        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
        }

        inner class Circle(var x: Int, var y: Int) {

            fun draw(paint: Paint, canvas: Canvas) {
                paint.color = Color.BLACK
                paint.style = Paint.Style.FILL
                canvas.drawCircle(
                        x.toFloat().toCenterAxisX(),
                        y.toFloat().toCenterAxisY(), 10.0f, paint)
            }
        }
    }
}
