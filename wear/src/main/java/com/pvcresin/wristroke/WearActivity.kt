package com.pvcresin.wristroke

import android.content.Intent
import android.os.Bundle
import android.support.wearable.activity.WearableActivity

class WearActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_wear)

        setAmbientEnabled()

        findViewById(R.id.button1).setOnClickListener {
            startActivity(Intent(applicationContext, ImageActivity::class.java))
        }

        findViewById(R.id.button2).setOnClickListener {
            startActivity(Intent(applicationContext, ListActivity::class.java))
        }

        findViewById(R.id.button3).setOnClickListener {
            startActivity(Intent(applicationContext, PaintActivity::class.java))
        }

        findViewById(R.id.button4).setOnClickListener {
            startActivity(Intent(applicationContext, MeasureActivity::class.java))
        }
    }
}
