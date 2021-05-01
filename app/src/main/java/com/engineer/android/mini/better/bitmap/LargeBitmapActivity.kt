package com.engineer.android.mini.better.bitmap

import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.R
import kotlinx.android.synthetic.main.activity_large_bitmap.*

class LargeBitmapActivity : AppCompatActivity() {
    private val TAG = "LargeBitmapActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_bitmap)

        val inputStream = assets.open("large_photo.jpeg")
        large_image_view.setInputStream(inputStream)


    }
}