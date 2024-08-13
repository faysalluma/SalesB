package com.groupec.salesb.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.google.gson.Gson
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.feature.home.HomeRoute


const val HOME_ROUTE = "home_route"

fun NavController.navigateToConfiguration(route: String) {
    navigate(route) {
        popUpTo(HOME_ROUTE){ inclusive = true }
    }
}

fun NavController.navigateTo(route: String) {
    navigate(route) {
        popUpTo(HOME_ROUTE){ inclusive = true }
    }
}

fun NavGraphBuilder.homeScreen(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    composable(route = HOME_ROUTE) {
        HomeRoute(navigateToConfiguration, navigateToLogin)
    }
}