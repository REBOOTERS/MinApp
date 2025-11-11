package com.engineer.android.mini.ui.behavior

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.engineer.android.mini.R
import java.io.IOException

class MusicPlayerActivity : AppCompatActivity() {

    private val TAG = "MusicPlayerActivity"
    private lateinit var playPauseButton: Button
    private var mediaPlayer: MediaPlayer? = null
    private var mIsPlaying = false
    // private val musicUrl = "https://storage.googleapis.com/exoplayer-test-media-0/play.mp3" // Removed online URL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_music_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playPauseButton = findViewById(R.id.play_pause_button)
        playPauseButton.setOnClickListener {
            if (mIsPlaying) {
                pauseMusic()
            } else {
                playMusic()
            }
        }
    }

    private fun playMusic() {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                try {
                    val assetFileDescriptor = assets.openFd("sample.mp3")
                    setDataSource(assetFileDescriptor.fileDescriptor, assetFileDescriptor.startOffset, assetFileDescriptor.length)
                    assetFileDescriptor.close()

                    setOnPreparedListener {
                        Log.d(TAG, "onPrepared")
                        start()
                        mIsPlaying = true
                        playPauseButton.text = "Pause"
                    }
                    setOnCompletionListener {
                        Log.d(TAG, "onCompletion")
                        mIsPlaying = false
                        playPauseButton.text = "Play"
                        mediaPlayer?.reset()
                        mediaPlayer = null
                    }
                    setOnErrorListener { _, what, extra ->
                        Log.e(TAG, "onError: what=$what, extra=$extra")
                        mIsPlaying = false
                        playPauseButton.text = "Play"
                        Toast.makeText(this@MusicPlayerActivity, "Error playing music", Toast.LENGTH_SHORT).show()
                        true
                    }
                    prepareAsync()
                    Log.d(TAG, "prepareAsync")
                } catch (e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG, "Error: sample.mp3 not found in assets")
                    Toast.makeText(this@MusicPlayerActivity, "Error: sample.mp3 not found in assets", Toast.LENGTH_LONG).show()
                    mediaPlayer = null
                }
            }
        } else {
            mediaPlayer?.start()
            mIsPlaying = true
            playPauseButton.text = "Pause"
            Log.d(TAG, "mediaPlayer resumed")
        }
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
        mIsPlaying = false
        playPauseButton.text = "Play"
        Log.d(TAG, "mediaPlayer paused")
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        Log.d(TAG, "mediaPlayer released")
    }
}