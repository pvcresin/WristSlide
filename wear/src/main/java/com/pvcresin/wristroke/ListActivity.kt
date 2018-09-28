package com.pvcresin.wristroke

import android.os.Bundle
import android.os.SystemClock
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.*
import android.widget.*

class ListActivity : WearableActivity() {

    private lateinit var wristrokeReceiver: WristokeReceiver

    internal var TAG = "WearActivity"

    internal lateinit var mGestureDetector: GestureDetector

    // touch
    val initPos = 200f
    var px = initPos
    var py = initPos

    val dur = 1 // duration
    var time = 0L
        get() = SystemClock.uptimeMillis()

    var star = 0

    var phase = 0
    var startTime = SystemClock.uptimeMillis()
    var testTime = SystemClock.uptimeMillis()
    var lastCenter = 0
    var totalNum = 40
    var scrollToTop = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setAmbientEnabled()

        fun initListView() {
            phase = 0

            setContentView(R.layout.activity_list)

            (findViewById(R.id.list) as ListView).apply {

                dividerHeight = 15

                val values = mutableListOf<String>()
                for (num in 0..totalNum - 1) {
                    if (num == star) values.add("star")
                    else values.add("" + num)
                }
                adapter = ArrayAdapter<String>(applicationContext,
                        android.R.layout.simple_list_item_1, values)

                if (scrollToTop) setSelection(totalNum - 1)

                setOnItemClickListener { adapterView, view, i, l ->
//                    val text = this.getItemAtPosition(i) as String
//                    Log.d(TAG, "i: $i text: $text")
                }

                setOnScrollListener(object: AbsListView.OnScrollListener {
                    override fun onScroll(view: AbsListView?, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
                        when(phase) {

                            1 -> {   // scrolling
                                val center = if (visibleItemCount == 4) firstVisibleItem + 1 else firstVisibleItem + 2
                                lastCenter = center

                                if (center == star) {
                                    testTime = SystemClock.uptimeMillis() - startTime
                                }
                            }
                        }
                    }

                    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {
                        if (phase == 0) {
                            startTime = SystemClock.uptimeMillis()
                            Log.d(TAG, "start!!")
                            phase++
                        } else if (phase == 1){
                            if (lastCenter == star) {
                                Log.d(TAG, "testTime: " + testTime)
                                Toast.makeText(context, "$testTime", Toast.LENGTH_LONG).show()
                                phase++
                            }
                        }
                    }
                })
            }
        }

        initListView()

        wristrokeReceiver = object : WristokeReceiver(this) {
            override fun test1(num: Int) {
                Log.d(TAG, "test to bottom $num")
                star = num
                scrollToTop = false
                initListView()
            }
            override fun test2(num: Int) {
                Log.d(TAG, "test to top $num")
                star = num
                scrollToTop = true
                initListView()
            }

            override fun onStartMoving() {
                dispatchTouchEvent(
                        MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_DOWN, px, py, 0))
            }
            override fun onMove(dx: Short, dy: Short) {
                px += dx
                py += dy
                dispatchTouchEvent(
                        MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_MOVE, px, py, 0))
            }
            override fun onStopMoving() {
                dispatchTouchEvent(
                        MotionEvent.obtain(time, time + dur, MotionEvent.ACTION_UP, px, py, 0))
                px = initPos
                py = initPos
            }
        }
        wristrokeReceiver.debug = true

        mGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(event: MotionEvent) {
                Log.d(TAG, "long tap")
                scrollToTop = !scrollToTop
                initListView()
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