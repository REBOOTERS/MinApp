package com.engineer.android.mini.net.hilt

import android.util.Log
import com.engineer.android.mini.MinApp
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


interface IFlyMachine {
    fun start()
    fun stop()
}

@Singleton
class IFlyMachineImpl : IFlyMachine {
    override fun start() {
        Log.d(TAG, "start() called")
    }

    override fun stop() {
        Log.d(TAG, "stop() called")
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class FlyMachineModule {
        @Singleton
        @Provides
        fun provideIFlyMachine(): IFlyMachine {
            return IFlyMachineImpl()
        }
    }

}

interface IVideoPlayer {
    fun play()
    fun pause()
    fun resume()
    fun stop()
}

@Singleton
class IVideoPlayerImpl : IVideoPlayer {
    override fun play() {
        Log.d(TAG, "play() called")
    }

    override fun pause() {
        Log.d(TAG, "pause() called")
    }

    override fun resume() {
        Log.d(TAG, "resume() called")
    }

    override fun stop() {
        Log.d(TAG, "stop() called")
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class VideoPlayerModule {

        @Singleton
        @Provides
        fun provideVideoPlayer(): IVideoPlayer {
            return IVideoPlayerImpl()
        }
    }
}

private const val TAG = "MiniEntryPoint"

@EntryPoint
@InstallIn(SingletonComponent::class)
interface MiniEntryPoint {
    fun flyMachine(): IFlyMachine

    fun videoPlayer(): IVideoPlayer
}


object MiniEntryHelper {
    var entryPoint =
        EntryPointAccessors.fromApplication(MinApp.INSTANCE, MiniEntryPoint::class.java)
}