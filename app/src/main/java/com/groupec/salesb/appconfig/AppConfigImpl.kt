package com.groupec.salesb.appconfig

import com.groupec.cleanarchitecture.core.config.AppConfig
import com.groupec.cleanarchitecture.core.config.AppFeatures
import com.groupec.salesb.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppConfigImpl @Inject constructor() : AppConfig {
    override val isDebug: Boolean = BuildConfig.DEBUG
    override val enableCrashReporting: Boolean
        get() = BuildConfig.ENABLE_CRASH_REPORTING && BuildConfig.CLIENT_CRASH_REPORTING_ENABLED
    override val clientId: String
        get() = BuildConfig.CLIENT_ID
    override val clientDisplayName: String
        get() = BuildConfig.CLIENT_DISPLAY_NAME
    override val isBusinessBuild: Boolean
        get() = BuildConfig.IS_BUSINESS_BUILD
    override val isStoreBuild: Boolean
        get() = BuildConfig.IS_STORE_BUILD
    override val serverUrl: String
        get() = if (BuildConfig.DEBUG) BuildConfig.DEBUG_SERVER_URL else BuildConfig.RELEASE_SERVER_URL
    override val networkTimeoutSeconds: Int
        get() = BuildConfig.NETWORK_TIMEOUT_SECONDS
    override val features: AppFeatures
        get() = AppFeatures(
            billingEnabled = BuildConfig.FEATURE_BILLING,
            subscriptionScreenEnabled = BuildConfig.FEATURE_SUBSCRIPTION_SCREEN,
            exportEnabled = BuildConfig.FEATURE_EXPORTS,
            receiptPrintingEnabled = BuildConfig.FEATURE_RECEIPT_PRINTING,
            quickSignupEnabled = BuildConfig.FEATURE_QUICK_SIGNUP,
            productLimitEnabled = BuildConfig.FEATURE_PRODUCT_LIMIT,
            saleLimitEnabled = BuildConfig.FEATURE_SALE_LIMIT,
            clientManagementEnabled = BuildConfig.FEATURE_CLIENTS,
            rayonManagementEnabled = BuildConfig.FEATURE_RAYONS,
        )
}
