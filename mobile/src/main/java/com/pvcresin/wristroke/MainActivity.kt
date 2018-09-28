package com.pvcresin.wristroke

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.SeekBar


class MainActivity : AppCompatActivity() {

    lateinit var wristrokeSender: WristrokeSender

    var alreadyLaunched = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wristrokeSender = WristrokeSender(this).apply { debug = true }

//        findViewById(R.id.button).setOnClickListener {
//            wristrokeSender.changeHand()
//        }
//
//        (findViewById(R.id.seekBar) as SeekBar).apply {
//            progress = (wristrokeSender.speed * 50).toInt()
//
//            setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener { // 0-100:0.0-2.0
//                override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
//                    wristrokeSender.changeSpeed(progress / 50.0f)
//                }
//                override fun onStartTrackingTouch(seekBar: SeekBar) {    }
//                override fun onStopTrackingTouch(seekBar: SeekBar) {    }
//            })
//        }

        (findViewById(R.id.start) as Button).setOnClickListener {
            if (!alreadyLaunched) {
                alreadyLaunched = true
                wristrokeSender.remoteIp = (findViewById(R.id.ip) as EditText).text.toString()
                wristrokeSender.remotePort = 6000
                wristrokeSender.connect()
            }
        }

        // back light always on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        supportActionBar?.hide()
    }

    override fun onResume() {
        super.onResume()
//        wristrokeSender.connect()
    }

    override fun onPause() {
        Log.d("wrist", "pause")
        wristrokeSender.disconnect()
        alreadyLaunched = false
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        android.os.Process.killProcess(android.os.Process.myPid())
    }
}
