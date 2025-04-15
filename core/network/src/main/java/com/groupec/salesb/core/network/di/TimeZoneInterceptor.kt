package com.groupec.salesb.core.network.di

import okhttp3.Interceptor
import okhttp3.Response
import java.util.TimeZone

class TimeZoneInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val timezone = TimeZone.getDefault().id
        val newRequest = chain.request().newBuilder()
            .addHeader("X-Timezone", timezone)
            .build()
        return chain.proceed(newRequest)
    }
}
