package com.engineer.music

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Singleton
class IMusicPlayerImpl : IMusicPlayer {
    private val TAG = "IMusicPlayerImpl"
    override fun play() {
        Log.d(TAG, "play() called")
    }

    override fun pause() {
        Log.d(TAG, "pause() called")
    }

    override fun stop() {
        Log.d(TAG, "stop() called")
    }

    override fun release() {
        Log.d(TAG, "release() called")
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class MusicPlayerModule {

        @Singleton
        @Provides
        fun provideMusicPlayer():IMusicPlayer {
            return IMusicPlayerImpl()
        }
    }
}