package com.engineer.mvp.mini

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.engineer.mvp.mini.focus.RecyclerViewPage

class RootPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val frameLayout = FrameLayout(this)
        setContentView(frameLayout)

        startActivity(Intent(this, RecyclerViewPage::class.java))
        finish()
    }
}