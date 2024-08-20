package com.groupec.salesb.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.feature.configuration.ConfigurationScreen
import com.groupec.feature.login.LoginScreen
import com.groupec.salesb.feature.home.HomeScreen
import com.groupec.salesb.feature.loading.LoadingScreen
import com.groupec.salesb.utils.FlipperNavigationLogger

@Composable
fun AppNavHost(
    modifier: Modifier,
    connectionState: Boolean,
    navController: NavHostController,
    startDestination: String = NavigationItem.Loading.route
) {
    LaunchedEffect (Unit) {
        val flipperPlugin = NavigationFlipperPlugin.getInstance()
        val flipperLogger = FlipperNavigationLogger(flipperPlugin)
        navController.addOnDestinationChangedListener(flipperLogger)
    }

    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        }
    ) {
        composable(NavigationItem.Loading.route) {
            LoadingScreen(
                navigateToConfiguration = {
                    navController.navigate(NavigationItem.Configuration.route) {
                        popUpTo(NavigationItem.Loading.route){ inclusive = true }
                    }
                },
                navigateToLogin = {
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Loading.route){ inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Loading.route){ inclusive = true }
                    }
                }
            )
        }

        composable(NavigationItem.Configuration.route) {
            ConfigurationScreen(
                navigateToLogin = {
                    navController.navigate(NavigationItem.Login.route) {
                        popUpTo(NavigationItem.Configuration.route){ inclusive = true }
                    }
                }
            )
        }

        composable(NavigationItem.Login.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Login.route){ inclusive = true }
                    }
                }
            )
        }

        composable(NavigationItem.Home.route) {
            HomeScreen()
        }
    }
}
