package com.groupec.cleanarchitecture.core.config

object AppConfigHolder {
    @Volatile
    private var appConfig: AppConfig? = null

    fun initialize(appConfig: AppConfig) {
        this.appConfig = appConfig
    }

    val current: AppConfig
        get() = appConfig ?: error("AppConfigHolder is not initialized")
}
