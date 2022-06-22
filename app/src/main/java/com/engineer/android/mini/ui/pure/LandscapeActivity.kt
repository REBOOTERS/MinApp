package com.engineer.android.mini.ui.pure

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
import android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R

class LandscapeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageView = ImageView(this)
//        imageView.scaleType = ImageView.ScaleType.FIT_XY
        window.decorView.systemUiVisibility = (SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
//        setDarkStatusBar()
        window.statusBarColor = Color.TRANSPARENT
        imageView.setImageResource(R.drawable.android)
        setContentView(imageView)


    }

    private fun setDarkStatusBar() {
        val flags = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

}