package com.pvcresin.wristroke

import android.os.Bundle
import android.os.SystemClock
import android.os.SystemClock.uptimeMillis
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.*

import jp.ogwork.gesturetransformableview.view.GestureTransformableImageView

class ImageActivity : WearableActivity() {

    private lateinit var wristrokeReceiver: WristokeReceiver

    internal var TAG = "WearActivity"

    internal lateinit var mGestureDetector: GestureDetector

    // touch
    val initPos = 200f
    var px = initPos
    var py = initPos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAmbientEnabled()

        wristrokeReceiver = object : WristokeReceiver(this) {
            override fun onStartMoving() {
                val t = uptimeMillis()
                dispatchTouchEvent(
                        MotionEvent.obtain(t, t, MotionEvent.ACTION_DOWN, px, py, 0))
            }
            override fun onMove(dx: Short, dy: Short) {
                px += dx
                py += dy
                val t = uptimeMillis()
                dispatchTouchEvent(
                        MotionEvent.obtain(t, t, MotionEvent.ACTION_MOVE, px, py, 0))
            }
            override fun onStopMoving() {
                val t = uptimeMillis()
                dispatchTouchEvent(
                        MotionEvent.obtain(t, t, MotionEvent.ACTION_UP, px, py, 0))
                px = initPos
                py = initPos
            }
        }

        wristrokeReceiver.debug = true

        fun initImageView() {
            setContentView(GestureTransformableImageView(applicationContext).apply {
                setImageResource(R.mipmap.lenna)
                setLimitScaleMax(400f)
            })
        }

        initImageView()

        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                Log.d(TAG, "long tap")
                initImageView()
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
