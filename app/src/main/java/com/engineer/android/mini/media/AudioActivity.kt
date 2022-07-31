package com.engineer.android.mini.media

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.engineer.android.mini.R
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import java.util.concurrent.TimeUnit

private const val TAG = "AudioActivity"

class AudioActivity : AppCompatActivity() {

    private lateinit var player: IjkMediaPlayer
    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio)

        setUpAudioPlayer()
    }

    private fun setUpAudioPlayer() {
        val url2 = "https://m3.8js.net/20220121/zaitaxiang-nvshengban.mp3"
        val url = "https://assets.mixkit.co/music/download/mixkit-tech-house-vibes-130.mp3"
        player = IjkMediaPlayer()
//        player.setOnPreparedListener {
//            Log.e(TAG, "OnPrepared() called")
//            it.start()
//            startCount()
//        }
//        player.setOnBufferingUpdateListener { mp, percent ->
//            Log.d(TAG, "OnBufferingUpdate() called with: mp = $mp, percent = $percent")
//        }
//        player.setOnTimedTextListener { iMediaPlayer, ijkTimedText ->
//            Log.e(
//                TAG,
//                "OnTimedText() called with: iMediaPlayer = $iMediaPlayer, ijkTimedText = $ijkTimedText"
//            )
//        }
//        player.setOnInfoListener { mp, what, extra ->
//            Log.d(TAG, "setUpAudioPlayer() called with: mp = $mp, what = $what, extra = $extra")
//            true
//        }
//        player.setOnCompletionListener {
//            Log.e(TAG, "OnCompletion() called")
//            stopCount()
//            player.reset()
//            player.setDataSource(this, Uri.parse(url))
//            player.prepareAsync()
//
//        }
//        player.setOnErrorListener { iMediaPlayer, i, i2 ->
//            Log.e(
//                TAG,
//                "OnError() called with: iMediaPlayer = $iMediaPlayer, i = $i, i2 = $i2"
//            )
//            true
//        }
        player.setDataSource(this, Uri.parse(url2))
        player.isLooping = false
        player.prepareAsync()


    }

    private fun startCount() {
        disposable = Observable.interval(1, TimeUnit.SECONDS)
            .doOnNext {
                Log.d(TAG, "AudioPlayer called ${player.dropFrameRate}")
            }
            .subscribe()
    }

    private fun stopCount() {
        disposable?.dispose()
    }

    override fun onPause() {
        super.onPause()
        player.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release()
        stopCount()
    }
}