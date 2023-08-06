package com.engineer.android.mini.net.hilt

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

interface AnalyticsService {
    fun analyticsMethods()
}

class AnalyticsMethodsImpl @Inject constructor() : AnalyticsService {
    override fun analyticsMethods() {
        println("this is analyticsMethods")
    }
}

//@Module
//@InstallIn(ActivityComponent::class)
//abstract class AnalyticsModule {
//
//    @Binds
//    abstract fun provideAnalyticsService(serviceImpl: AnalyticsMethodsImpl): AnalyticsService
//}

@Module
@InstallIn(ActivityComponent::class)
class AnalyticsModule2 {

    @Provides
    fun provideAnalyticsService():AnalyticsService {
        return AnalyticsMethodsImpl()
    }
}