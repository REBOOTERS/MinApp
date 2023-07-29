package com.engineer.android.mini.ui.pure

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.engineer.android.mini.R
import com.engineer.common.widget.cropper.CropImageView

class CropActivity : AppCompatActivity() {
    private val TAG = "CropActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        val cropImage = findViewById<CropImageView>(R.id.cropImageView)
        cropImage.imageResource = R.drawable.wallpaper_portrait

        cropImage.setOnSetCropOverlayMovedListener {
            Log.d(TAG, "onCreate() called $it")
        }

        findViewById<View>(R.id.confirm).setOnClickListener {

        }
    }
}