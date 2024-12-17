package com.groupec.salesb.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.feature.configuration.ConfigurationScreen
import com.groupec.feature.login.LoginScreen
import com.groupec.salesb.feature.changepassword.ChangePasswordScreen
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
    LaunchedEffect(Unit) {
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
                        popUpTo(NavigationItem.Loading.route) { inclusive = true }
                    }
                },
                navigateToLogin = { raisonSociale ->
                    navController.navigate(NavigationItem.Login.route.plus("/${raisonSociale}")) {
                        popUpTo(NavigationItem.Loading.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Loading.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavigationItem.Configuration.route) {
            ConfigurationScreen(
                navigateToLogin = { raisonSociale ->
                    navController.navigate(NavigationItem.Login.route.plus("/${raisonSociale}")) {
                        popUpTo(NavigationItem.Configuration.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationItem.Login.route.plus("/{raisonSociale}"),
            arguments = listOf(
                navArgument("raisonSociale") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val raisonSociale = backStackEntry.arguments?.getString("raisonSociale") ?: ""
            LoginScreen(
                raisonSociale = raisonSociale,
                navigateToChangePassword = { userId, firstLogin ->
                    navController.navigate(NavigationItem.ChangePassword.route.plus("/${userId}/${firstLogin}"))
                },
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Login.route.plus("/{raisonSociale}")) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationItem.ChangePassword.route.plus("/{userId}/{firstLogin}"),
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                },
                navArgument("firstLogin") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val firstLogin = backStackEntry.arguments?.getBoolean("firstLogin") ?: false
            ChangePasswordScreen(
                userId = userId,
                firstLogin = firstLogin,
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                       popUpTo(NavigationItem.Login.route.plus("/{raisonSociale}")) { inclusive = true }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(NavigationItem.Home.route) {
            HomeScreen()
        }

        composable(NavigationItem.Sale.route) {
            Text("Sale")
        }

        composable(NavigationItem.Product.route) {
            Text("Product")
        }
    }
}
