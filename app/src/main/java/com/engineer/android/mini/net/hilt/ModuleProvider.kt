package com.engineer.android.mini.net.hilt

import android.app.Activity
import android.content.Context
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityRxCacheBinding
import com.zchu.rxcache.RxCache
import com.zchu.rxcache.diskconverter.GsonDiskConverter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import java.io.File

@Module
@InstallIn(ActivityComponent::class)
object RxCacheProvider {

    @Provides
    fun provideRxCache(@ActivityContext context: Context): RxCache {
        return RxCache.Builder()
            .appVersion(7)
            .diskConverter(GsonDiskConverter())
            .diskDir(File(context.cacheDir.toString() + File.separator + context.getString(R.string.app_name)))
            .setDebug(true)
            .build()
    }
}

@Module
@InstallIn(ActivityComponent::class)
object ViewBindingProvider {

    @Provides
    fun provideViewBinding(@ActivityContext context: Context): ActivityRxCacheBinding {
        val activity = context as Activity
        return ActivityRxCacheBinding.inflate(activity.layoutInflater)
    }
}