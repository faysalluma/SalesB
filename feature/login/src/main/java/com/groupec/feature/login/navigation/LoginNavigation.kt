package com.groupec.feature.login.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.groupec.feature.login.LoginRoute


const val LOGIN_ROUTE = "login_route"

fun NavController.navigateToHome(route: String) {
    navigate(route) {
        popUpTo(LOGIN_ROUTE){ inclusive = true }
    }
}

fun NavGraphBuilder.loginScreen(
    navigateToHome: () -> Unit
) {
    composable(route = LOGIN_ROUTE) {
        LoginRoute(navigateToHome)
    }
}