package com.groupec.salesb

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.android.utils.FlipperUtils
import com.facebook.flipper.plugins.inspector.DescriptorMapping
import com.facebook.flipper.plugins.inspector.InspectorFlipperPlugin
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.flipper.plugins.sharedpreferences.SharedPreferencesFlipperPlugin
import com.facebook.soloader.SoLoader
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initFlipper(this)
    }
}

private fun initFlipper (context: Context) {
    SoLoader.init(context, false)

    if (isDebugBuild(context) && FlipperUtils.shouldEnableFlipper(context)) {
        // Setup Flipper
        val client = AndroidFlipperClient.getInstance(context)
        client.addPlugin(InspectorFlipperPlugin(context, DescriptorMapping.withDefaults()))

        // Add Network plugin
        val networkPlugin = NetworkFlipperPlugin()
        client.addPlugin(networkPlugin)

        // Add Navigation plugin
        client.addPlugin(NavigationFlipperPlugin.getInstance())

        // Start client
        client.start()
    }
}

fun isDebugBuild(context: Context): Boolean {
    return context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
}