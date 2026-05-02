package com.groupec.salesb.core.network

import com.groupec.salesb.core.network.di.TimeZoneInterceptor
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.TimeZone

class TimeZoneInterceptorTest {

    @Test
    fun `intercept should add current timezone header`() {
        val initialTimeZone = TimeZone.getDefault()
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Paris"))
        try {
            val chain = RecordingChain(
                Request.Builder()
                    .url("https://salesb.test")
                    .build()
            )

            TimeZoneInterceptor().intercept(chain)

            assertEquals("Europe/Paris", chain.proceededRequest.header("X-Timezone"))
        } finally {
            TimeZone.setDefault(initialTimeZone)
        }
    }

    private class RecordingChain(
        private val request: Request
    ) : Interceptor.Chain {
        lateinit var proceededRequest: Request

        override fun request(): Request = request

        override fun proceed(request: Request): Response {
            proceededRequest = request
            return Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }

        override fun connection() = null

        override fun call() = throw UnsupportedOperationException()

        override fun connectTimeoutMillis(): Int = 0

        override fun withConnectTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

        override fun readTimeoutMillis(): Int = 0

        override fun withReadTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this

        override fun writeTimeoutMillis(): Int = 0

        override fun withWriteTimeout(timeout: Int, unit: java.util.concurrent.TimeUnit): Interceptor.Chain = this
    }
}
