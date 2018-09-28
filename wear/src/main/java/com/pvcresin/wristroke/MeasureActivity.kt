package com.pvcresin.wristroke

import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.TextView

class MeasureActivity : WearableActivity() {

    private lateinit var wristrokeReceiver: WristokeReceiver

    internal var TAG = "WearActivity"

    internal lateinit var mGestureDetector: GestureDetector

    val initPos = 0f
    var px = initPos
    var py = initPos

    lateinit var tvX: TextView
    lateinit var tvY: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAmbientEnabled()

        setContentView(R.layout.activity_measure)

        tvX = findViewById(R.id.xAxis) as TextView
        tvY = findViewById(R.id.yAxis) as TextView

        fun initMeasure() {
            px = initPos
            py = initPos
            tvX.text = "" + initPos
            tvY.text = "" + initPos
        }

        initMeasure()


        fun mickey2Inch(float: Float): Float = float * 0.01f

        fun inch2Cm(float: Float): Float = float * 2.54f

        wristrokeReceiver = object : WristokeReceiver(this) {
            override fun onStartMoving() {

            }
            override fun onMove(dx: Short, dy: Short) {
//                px += inch2Cm(mickey2Inch(dx.toFloat()))
//                py += inch2Cm(mickey2Inch(dy.toFloat()))
                px += dx
                py += dy

                tvX.text = "$px mm"
                tvY.text = "$py cm"
            }
            override fun onStopMoving() {

            }
        }
        wristrokeReceiver.debug = true

        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                Log.d(TAG, "long tap")
                initMeasure()
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
}
