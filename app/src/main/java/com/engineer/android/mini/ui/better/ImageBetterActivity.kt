package com.engineer.android.mini.ui.better

import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.R

private const val TAG = "Better"

class ImageBetterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_better)

        val _1MB = 1024 * 1024f

        val base = 1200 * 800 * 4
        Log.e(TAG, "onCreate: base = $base")
        val density = Resources.getSystem().displayMetrics
        Log.e(TAG, "onCreate: display = $density")
        val real = (480f / 640)
        Log.e(TAG, "onCreate: real = $real")
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.android)
        Log.e(TAG, "onCreate: bitmap size =${bitmap.allocationByteCount / _1MB} MB")
    }
}