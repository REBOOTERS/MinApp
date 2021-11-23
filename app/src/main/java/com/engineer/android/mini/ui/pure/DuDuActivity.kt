package com.engineer.android.mini.ui.pure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import com.engineer.android.mini.R

class DuDuActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_du_du)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        Log.e("DuDuActivity", "onTouchEvent: ${event?.action}")
        return super.onTouchEvent(event)
    }
}