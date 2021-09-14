package com.engineer.android.mini.media

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.widget.MediaController
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMediaBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import java.io.File

class MediaActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityMediaBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/Movies/vv.mp4"

    private var landscape = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        if (File(path).exists()) {
            viewBinding.videoView.setVideoPath(path)
            val control = MediaController(this)
            viewBinding.videoView.setMediaController(control)
            viewBinding.videoView.requestFocus()
        } else {
            "$path not exist".toast()
        }

        viewBinding.fullScreen.setOnClickListener {
            if (landscape) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                viewBinding.fullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_24)
            } else {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                viewBinding.fullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_exit_24)
            }
            landscape = !landscape
        }
    }
}