package com.engineer.android.mini.coroutines.old

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class InterfacesProvider {

    @Singleton
    @Provides
    fun provideNetWork(): MainNetwork {
        return getNetworkService()
    }

    @Provides
    fun provideTitleDao(@ApplicationContext context: Context): TitleDao {
        val database = getDatabase(context)
        return database.titleDao
    }

    @Provides
    fun providerImageDao(@ApplicationContext context: Context): ImageDao {
        val database = getDatabase(context)
        return database.imageDao
    }


}