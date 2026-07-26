package com.groupec.salesb.core

import com.groupec.cleanarchitecture.core.config.AppConfigHolder

class Constants {
    companion object {
        // For emulator user 10.0.2.2 --- (192, 172) for wampserver --- Don't use 10.188 ...
        // const val SERVER_URL = "http://192.168.1.69/SalesBStoreApi/"
        // const val SERVER_URL = "https://salesbapi.groupec.net/"
        val SERVER_URL: String
            get() = AppConfigHolder.current.serverUrl

        val IS_DEBUG: Boolean
            get() = AppConfigHolder.current.isDebug

        val NETWORK_TIMEOUT_SECONDS: Long
            get() = AppConfigHolder.current.networkTimeoutSeconds.toLong()

        val BASE_URL: String
            get() = joinUrl(
                SERVER_URL,
                "public/${AppConfigHolder.current.backendId}/",
            )
        val UPLOAD_URL: String
            get() = joinUrl(
                SERVER_URL,
                "includes/config/${AppConfigHolder.current.backendId}/uploads/",
            )
        const val APP_LINK = "https://www.salesb.groupec.net/resetapppassword"
    }
}

private fun joinUrl(serverUrl: String, path: String): String =
    "${serverUrl.trimEnd('/')}/${path.trimStart('/')}"
