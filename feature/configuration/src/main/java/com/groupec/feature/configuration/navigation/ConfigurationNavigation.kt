package com.groupec.feature.configuration.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.groupec.feature.configuration.ConfigurationRoute


const val CONFIGURATION_ROUTE = "configuration_route"

fun NavController.navigateToLogin(route: String) {
   navigate(route) {
       popUpTo(CONFIGURATION_ROUTE){ inclusive = true }
   }
}

fun NavGraphBuilder.configurationScreen(
    navigateToLogin: () -> Unit
) {
    composable(route = CONFIGURATION_ROUTE) {
        ConfigurationRoute(navigateToLogin)
    }
}