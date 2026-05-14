package com.groupec.cleanarchitecture.core.config

interface AppConfig {
    val isDebug: Boolean
    val enableCrashReporting: Boolean // For CrashLytics
    val clientId: String
    val clientDisplayName: String
    val isBusinessBuild: Boolean
    val isStoreBuild: Boolean
    val serverUrl: String
    val networkTimeoutSeconds: Int
    val features: AppFeatures
}

data class AppFeatures(
    val billingEnabled: Boolean,
    val subscriptionScreenEnabled: Boolean,
    val exportEnabled: Boolean,
    val receiptPrintingEnabled: Boolean,
    val quickSignupEnabled: Boolean,
    val productLimitEnabled: Boolean,
    val saleLimitEnabled: Boolean,
    val clientManagementEnabled: Boolean,
    val rayonManagementEnabled: Boolean,
)
