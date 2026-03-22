package com.groupec.salesb.core

import com.groupec.cleanarchitecture.core.config.AppConfigHolder

class Constants {
    companion object {
        // For emulator user 10.0.2.2 --- (192, 172) for wampserver --- Don't use 10.188 ...
        // const val SERVER_URL = "http://192.168.1.69/SalesBStoreApi/"
        // const val SERVER_URL = "https://salesbapi.groupec.net/"
        val SERVER_URL: String
            get() = AppConfigHolder.current.serverUrl

        val NETWORK_TIMEOUT_SECONDS: Long
            get() = AppConfigHolder.current.networkTimeoutSeconds.toLong()

        val BASE_URL: String
            get() = SERVER_URL.plus("public/")
        val UPLOAD_URL: String
            get() = SERVER_URL.plus("includes/config/default/uploads/")
        const val APP_LINK = "https://www.salesb.groupec.net/resetapppassword"
    }
}
