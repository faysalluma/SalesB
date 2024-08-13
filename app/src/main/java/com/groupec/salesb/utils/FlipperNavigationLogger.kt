package com.groupec.salesb.utils

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.feature.configuration.navigation.CONFIGURATION_ROUTE
import com.groupec.feature.login.navigation.LOGIN_ROUTE
import com.groupec.salesb.feature.home.navigation.HOME_ROUTE

class FlipperNavigationLogger(private val flipperPlugin: NavigationFlipperPlugin) : NavController.OnDestinationChangedListener {
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        val route = destination.route
        // Log the navigation event to Flipper
        flipperPlugin.sendNavigationEvent(route, controller.currentDestinationClassName(), null)
    }
}

fun NavController.currentDestinationClassName(): String? {
    val route = currentBackStackEntry?.destination?.route
    return when (route?.substringBeforeLast("/")?.substringBeforeLast("?")) {
        CONFIGURATION_ROUTE -> featurePackage.plus(".ConfigurationScreen")
        LOGIN_ROUTE -> featurePackage.plus(".LoginScreen")
        HOME_ROUTE -> featurePackage.plus(".HomeScreen")
        else -> null
    }
}

const val featurePackage = "com.groupec.salesb.feature"