package com.groupec.salesb.utils

/*
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.salesb.navigation.NavigationItem

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
        NavigationItem.Loading.route -> featurePackage.plus(".LoadingScreen")
        NavigationItem.Configuration.route -> featurePackage.plus(".ConfigurationScreen")
        NavigationItem.Login.route -> featurePackage.plus(".LoginScreen")
        NavigationItem.ChangePassword.route -> featurePackage.plus(".ChangePasswordScreen")
        NavigationItem.Home.route -> featurePackage.plus(".HomeScreen")
        else -> null
    }
}

const val featurePackage = "com.groupec.salesb.feature"*/
