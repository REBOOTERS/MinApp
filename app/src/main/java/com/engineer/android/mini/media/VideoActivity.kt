package com.engineer.android.mini.media

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityMediaBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import java.io.File
import java.lang.StringBuilder

class VideoActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityMediaBinding
    val path = Environment.getExternalStorageDirectory().absolutePath + "/Movies/tt.mp4"
    private val netVideoPath = "https://cdn.videvo.net/videvo_files/video/free/2020-07" +
            "/large_watermarked/06_1596083776_preview.mp4"

    private var landscape = false
    private lateinit var control: MediaController

    private var receiver: MyVolumeReceiver? = null
    private val action = "android.media.VOLUME_CHANGED_ACTION"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMediaBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        hideNavigationBar()
        setMediaInfo()
        if (File(path).exists()) {
            viewBinding.videoView.setVideoPath(path)
        } else {
            viewBinding.videoView.setVideoURI(Uri.parse(netVideoPath))
        }
        val listener = object : VideoPlayAdapterListener {
            override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                "$path not exist".toast()
                viewBinding.imageView.setImageResource(R.drawable.spring)
                return super.onError(mp, what, extra)
            }

            override fun onPrepared(mp: MediaPlayer?) {
                super.onPrepared(mp)
                mp?.setVolume(0.2f, 0.2f)
                mp?.start()
                mp?.setOnVideoSizeChangedListener(this)
                mp?.isLooping = false
                control.show(Int.MAX_VALUE)
            }
        }
        viewBinding.videoView.setOnErrorListener(listener)
        viewBinding.videoView.setOnCompletionListener(listener)
        viewBinding.videoView.setOnPreparedListener(listener)
        viewBinding.videoView.setOnInfoListener(listener)

        control = MediaController(this)

        viewBinding.videoView.setMediaController(control)
        viewBinding.videoView.requestFocus()
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

    private fun hideNavigationBar() {

        val uiFlag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.decorView.systemUiVisibility = uiFlag
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