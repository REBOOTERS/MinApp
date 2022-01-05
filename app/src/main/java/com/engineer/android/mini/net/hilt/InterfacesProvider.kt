package com.engineer.android.mini.net.hilt

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import javax.inject.Inject

interface AnalyticsService {
    fun analyticsMethods()
}

class analyticsMethodsImpl @Inject constructor() : AnalyticsService {
    override fun analyticsMethods() {
        println("this is analyticsMethods")
    }
}

@Module
@InstallIn(ActivityComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun provideAnalyticsService(serviceImpl: analyticsMethodsImpl): AnalyticsService
}