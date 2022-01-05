package com.engineer.android.mini.net.hilt

import com.engineer.android.mini.MinApp
import com.engineer.book.interfaces.IBookManager
import com.engineer.music.IMusicPlayer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent


@EntryPoint
@InstallIn(SingletonComponent::class)
interface MiniEntryPoint {


    fun bookManager(): IBookManager

    fun musicPlayer(): IMusicPlayer
}


object MiniEntryHelper {
    var entryPoint =
        EntryPointAccessors.fromApplication(MinApp.INSTANCE, MiniEntryPoint::class.java)
}