package com.engineer.android.mini.media

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.R
import tv.danmaku.ijk.media.player.IjkMediaPlayer

private const val TAG = "AudioActivity"

class AudioActivity : AppCompatActivity() {

    private lateinit var player: IjkMediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        setUpAudioPlayer()
    }

    private fun setUpAudioPlayer() {
        val url = "https://assets.mixkit.co/music/download/mixkit-tech-house-vibes-130.mp3"
        player = IjkMediaPlayer()
        player.setOnPreparedListener {
            Log.e(TAG, "OnPrepared() called")
            it.start()
        }
        player.setOnBufferingUpdateListener { mp, percent ->
            Log.d(TAG, "OnBufferingUpdate() called with: mp = $mp, percent = $percent")
        }
        player.setOnTimedTextListener { iMediaPlayer, ijkTimedText ->
            Log.e(
                TAG,
                "OnTimedText() called with: iMediaPlayer = $iMediaPlayer, ijkTimedText = $ijkTimedText"
            )
        }
        player.setOnCompletionListener {
            Log.e(TAG, "OnCompletion() called")
        }
        player.setOnErrorListener { iMediaPlayer, i, i2 ->
            Log.e(
                TAG,
                "OnError() called with: iMediaPlayer = $iMediaPlayer, i = $i, i2 = $i2"
            )
            true
        }
        player.setDataSource(this, Uri.parse(url))
        player.isLooping = false
        player.prepareAsync()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
    }
}