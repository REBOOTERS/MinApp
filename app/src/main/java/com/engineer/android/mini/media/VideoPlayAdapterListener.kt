package com.engineer.android.mini.media

import android.media.MediaPlayer
import android.util.Log

private const val TAG = "VideoPlayAdapter"

interface VideoPlayAdapterListener : MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener,
    MediaPlayer.OnVideoSizeChangedListener {


    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onError() called with: mp = $mp, what = $what, extra = $extra")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        Log.d(TAG, "onPrepared() called with: mp = $mp")
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Log.d(TAG, "onCompletion() called with: mp = $mp")
    }

    override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.d(TAG, "onInfo() called with: mp = $mp, what = $what, extra = $extra")
        return true
    }

    override fun onVideoSizeChanged(mp: MediaPlayer?, width: Int, height: Int) {
        Log.d(TAG, "onVideoSizeChanged() called with: mp = $mp, width = $width, height = $height")
    }

}