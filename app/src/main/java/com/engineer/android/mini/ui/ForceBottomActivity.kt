package com.engineer.android.mini.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import com.engineer.android.mini.R

private const val TAG = "ForceBottomActivity"
class ForceBottomActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_force_bottom)

        val ll = findViewById<LinearLayout>(R.id.ll_container)
        for (i in 0..15) {
            val textView = TextView(this)
            textView.text = i.toString()
            ll.addView(textView)
        }

        val ok = !TextUtils.isEmpty(null)
        Log.d(TAG, "onCreate() called with: $ok")
    }
}