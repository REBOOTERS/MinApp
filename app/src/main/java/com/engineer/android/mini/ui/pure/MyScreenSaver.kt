package com.engineer.android.mini.ui.pure

import android.content.Intent
import android.service.dreams.DreamService
import android.view.View
import com.engineer.android.mini.R
import com.engineer.android.mini.RootActivity

class MyScreenSaver : DreamService() {

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Exit dream upon user touch
        isInteractive = true
        // Hide system UI
        isFullscreen = true
        // Set the dream layout
        setContentView(R.layout.my_day_dream)

        findViewById<View>(R.id.click_text).setOnClickListener {
            val intent = Intent(this, RootActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}