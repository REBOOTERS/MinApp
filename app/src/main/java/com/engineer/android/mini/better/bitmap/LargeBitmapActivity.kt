package com.engineer.android.mini.better.bitmap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R

class LargeBitmapActivity : AppCompatActivity() {
    private val TAG = "LargeBitmapActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_bitmap)
        val largeImageView = findViewById<LargeImageView>(R.id.large_image_view)
        val inputStream = assets.open("large_photo.jpeg")
        largeImageView.setInputStream(inputStream)


    }
}