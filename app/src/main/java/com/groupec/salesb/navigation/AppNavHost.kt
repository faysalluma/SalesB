package com.groupec.salesb.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.feature.configuration.navigation.CONFIGURATION_ROUTE
import com.groupec.feature.configuration.navigation.configurationScreen
import com.groupec.feature.configuration.navigation.navigateToLogin
import com.groupec.feature.login.navigation.LOGIN_ROUTE
import com.groupec.feature.login.navigation.loginScreen
import com.groupec.feature.login.navigation.navigateToHome
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.feature.home.navigation.HOME_ROUTE
import com.groupec.salesb.feature.home.navigation.homeScreen
import com.groupec.salesb.feature.home.navigation.navigateTo
import com.groupec.salesb.feature.home.navigation.navigateToConfiguration
import com.groupec.salesb.utils.FlipperNavigationLogger

@Composable
fun AppNavHost(
    modifier: Modifier,
    connectionState: Boolean,
    navController: NavHostController,
    startDestination: String = HOME_ROUTE
) {
    LaunchedEffect (Unit) {
        val flipperPlugin = NavigationFlipperPlugin.getInstance()
        val flipperLogger = FlipperNavigationLogger(flipperPlugin)
        navController.addOnDestinationChangedListener(flipperLogger)
    }
    /*val startDestination = when {
        userState != null -> LOGIN_ROUTE
        parameterState != null -> HOME_ROUTE
        else -> CONFIGURATION_ROUTE
    }*/

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        /*      enterTransition = { EnterNone },
              exitTransition = { ExitNone }*/
    ) {
        homeScreen(
            navigateToConfiguration = {
                navController.navigateToConfiguration(CONFIGURATION_ROUTE)
            },
            navigateToLogin = {
                navController.navigateTo(LOGIN_ROUTE)
            }
        )
        configurationScreen(
            navigateToLogin = {
                navController.navigateToLogin(LOGIN_ROUTE)
            }
        )
        loginScreen(
            navigateToHome = {
                navController.navigateToHome(HOME_ROUTE)
            }
        )
    }
}
