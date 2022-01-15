package com.engineer.android.mini.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.widget.MediaController
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMediaBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import java.io.File
import java.lang.StringBuilder

class VideoActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityMediaBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/Movies/vv.mp4"

    private var landscape = false

    private var receiver: MyVolumeReceiver? = null
    private val action = "android.media.VOLUME_CHANGED_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setMediaInfo()

        viewBinding.videoView.setVideoPath(path)
        viewBinding.videoView.setOnErrorListener { mp, what, extra ->
            Log.e(TAG, "onError() called with: mp = $mp, what = $what, extra = $extra")
            "$path not exist".toast()
            viewBinding.imageView.setImageResource(R.drawable.spring)
            true
        }
        val control = MediaController(this)
        viewBinding.videoView.setMediaController(control)
        viewBinding.videoView.requestFocus()
        viewBinding.videoView.setOnPreparedListener {
            Log.e(TAG, "onPrepared() called")
            it.setVolume(0.2f, 0.2f)
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

        register()
    }

    private fun register() {
        receiver = MyVolumeReceiver()
        receiver?.let {
            val filter = IntentFilter()
            filter.addAction(action)
            registerReceiver(it, filter)
        }
    }

    override fun onBackPressed() {
        if (landscape) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            viewBinding.fullScreen.setImageResource(R.drawable.ic_baseline_fullscreen_24)
            landscape = !landscape
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        receiver?.let {
            unregisterReceiver(it)
        }
    }

    private fun setMediaInfo() {
        val am = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val music = am.getStreamVolume(AudioManager.STREAM_MUSIC)
        val alarm = am.getStreamVolume(AudioManager.STREAM_ALARM)
        val ring = am.getStreamVolume(AudioManager.STREAM_RING)
        val system = am.getStreamVolume(AudioManager.STREAM_SYSTEM)

        val sb = StringBuilder()
        sb.append("music  volume : $music").append("\n")
        sb.append("alarm  volume : $alarm").append("\n")
        sb.append("ring   volume : $ring").append("\n")
        sb.append("system volume : $system").append("\n")

        viewBinding.mediaInfo.text = sb.toString()

    }

    private inner class MyVolumeReceiver : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            if (p1?.action?.equals(action) == true) {
                setMediaInfo()
            }
        }
    }
}