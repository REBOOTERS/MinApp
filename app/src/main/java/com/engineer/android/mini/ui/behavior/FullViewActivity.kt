package com.engineer.android.mini.ui.behavior

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.widget.ImageView
import com.engineer.android.mini.R

private const val TAG = "FullViewActivity"

class FullViewActivity : AppCompatActivity() {
    private var fullscreen = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_view)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.decorView?.windowInsetsController?.hide(WindowInsets.Type.statusBars())
            window?.decorView?.windowInsetsController?.hide(WindowInsets.Type.navigationBars())
        }
        findViewById<ImageView>(R.id.image_content).setOnClickListener {
            Log.d(TAG, "onClick called $fullscreen")
            if (fullscreen) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window?.decorView?.windowInsetsController?.show(WindowInsets.Type.statusBars())
                    window?.decorView?.windowInsetsController?.show(WindowInsets.Type.navigationBars())
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    window?.decorView?.windowInsetsController?.hide(WindowInsets.Type.statusBars())
                    window?.decorView?.windowInsetsController?.hide(WindowInsets.Type.navigationBars())
                }
            }
            fullscreen = !fullscreen
        }
    }


}