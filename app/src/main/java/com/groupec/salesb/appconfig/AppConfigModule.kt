package com.groupec.cleanarchitecturesampleapp.appconfig

import com.groupec.cleanarchitecture.core.config.AppConfig
import com.groupec.salesb.appconfig.AppConfigImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppConfigModule {
    @Provides
    @Singleton
    fun provideAppConfig(): AppConfig = AppConfigImpl()
}
