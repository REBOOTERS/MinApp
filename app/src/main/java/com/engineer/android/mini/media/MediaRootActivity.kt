package com.engineer.android.mini.media

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.gotoActivity

class MediaRootActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_root)
        findViewById<View>(R.id.video).setOnClickListener {
            gotoActivity(VideoActivity::class.java)
        }
        findViewById<View>(R.id.audio).setOnClickListener {
            gotoActivity(AudioActivity::class.java)
        }
    }
}