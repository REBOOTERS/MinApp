package com.engineer.android.mini.media

import android.content.res.Configuration
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivitySimpleVideoBinding

class SimpleVideoActivity : AppCompatActivity() {
    private val TAG = "SimpleVideoActivity"
    val path = Environment.getExternalStorageDirectory().absolutePath + "/Movies/tt.mp4"
    val path2 = Environment.getExternalStorageDirectory().absolutePath + "/Movies/vv.mp4"

    private lateinit var viewBinding: ActivitySimpleVideoBinding

    private var player1: MediaPlayer? = null
    private var player2: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivitySimpleVideoBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        translucentStatusBar(viewBinding.rootViewContent)
        val listener = object : VideoPlayAdapterListener {
            override fun onPrepared(mp: MediaPlayer?) {
                super.onPrepared(mp)
                Log.d(TAG, "onPrepared() called with: mp = $mp")
                mp?.let {
                    player1 = it
                    it.start()
                }
            }

            override fun onCompletion(mp: MediaPlayer?) {
                super.onCompletion(mp)
                Log.d(TAG, "onCompletion() called with: mp = $mp")
            }
        }
        viewBinding.videoView1.setOnPreparedListener(listener)
        viewBinding.videoView1.setOnCompletionListener(listener)
        viewBinding.videoView1.setVideoPath(path2)
        viewBinding.videoView1.setBackgroundColor(Color.TRANSPARENT)

        viewBinding.videoView2.setOnPreparedListener {
            Log.d(TAG, "onPrepared2() called")
            player2 = it
        }
        viewBinding.videoView2.setVideoPath(path)

        viewBinding.rootViewContent.setOnClickListener {
            player1?.stop()
            player2?.start()
            player2?.setOnInfoListener { mp, what, extra ->
                Log.d(TAG, "onInfo() called with: mp = $mp, what = $what, extra = $extra")
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    viewBinding.videoView1.visibility = View.GONE
                }

                false
            }
            viewBinding.rootViewContent.setOnClickListener(null)
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        Log.d(TAG, "onConfigurationChanged() called with: newConfig = $newConfig")
    }

    private fun translucentStatusBar(frameLayout: View) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT
        val controller = WindowInsetsControllerCompat(window, frameLayout)

        controller.isAppearanceLightStatusBars = false
        window.navigationBarColor = Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.navigationBarDividerColor = Color.TRANSPARENT
        }

        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

    override fun onDestroy() {
        super.onDestroy()
        player1?.release()
        player2?.release()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

}