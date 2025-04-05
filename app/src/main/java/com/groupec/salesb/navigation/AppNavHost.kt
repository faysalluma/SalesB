package com.groupec.salesb.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
// import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
import com.groupec.feature.configuration.ConfigurationScreen
import com.groupec.feature.forgotpassword.ForgotPasswordScreen
import com.groupec.feature.login.LoginScreen
import com.groupec.feature.productdetail.ProductDetailScreen
import com.groupec.feature.productlist.ProductListScreen
import com.groupec.feature.sale.SaleScreen
import com.groupec.feature.salechart.SaleChartScreen
import com.groupec.feature.salelist.SaleListScreen
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.feature.changepassword.ChangePasswordScreen
import com.groupec.salesb.feature.home.HomeScreen
import com.groupec.salesb.feature.loading.LoadingScreen
// import com.groupec.salesb.utils.FlipperNavigationLogger
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    connectionState: Boolean,
    navController: NavHostController,
    startDestination: String = NavigationItem.Loading.route
) {
    /*LaunchedEffect(Unit) {
        val flipperPlugin = NavigationFlipperPlugin.getInstance()
        val flipperLogger = FlipperNavigationLogger(flipperPlugin)
        navController.addOnDestinationChangedListener(flipperLogger)
    }*/

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
        composable(
            route = NavigationItem.Loading.route,
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = Constants.APP_LINK.plus("/{requestChangePwdByEmail}")
                }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("requestChangePwdByEmail") ?: ""
            if (email.isNotEmpty()) {
                LaunchedEffect(Unit) {
                    navController.navigate(NavigationItem.ChangePassword.route.plus("?firstLogin=true&email=$email"))
                }
            } else {
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
                    navController.navigate(NavigationItem.ChangePassword.route.plus("?userId={userId}&firstLogin={firstLogin}"))
                },
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Login.route.plus("/{raisonSociale}")) {
                            inclusive = true
                        }
                    }
                },
                navigateToForgotPassword = { navController.navigate(NavigationItem.ForgotPassword.route) }
            )
        }


        composable(
            route = NavigationItem.ChangePassword.route.plus("?userId={userId}&firstLogin={firstLogin}&email={email}"),
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                    defaultValue = 0
                },
                navArgument("firstLogin") {
                    type = NavType.BoolType
                    defaultValue = false
                },
                navArgument("email") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val firstLogin = backStackEntry.arguments?.getBoolean("firstLogin") ?: false
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ChangePasswordScreen(
                userId = userId,
                firstLogin = firstLogin,
                email = email,
                navigateToHome = {
                    navController.navigate(NavigationItem.Home.route) {
                        popUpTo(NavigationItem.Login.route.plus("/{raisonSociale}")) {
                            inclusive = true
                        }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                },
                navigateToStartDestination = {
                    navController.navigate(NavigationItem.Loading.route) {
                        // Delete the entire background stack
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(NavigationItem.Home.route) {
            HomeScreen(
                navigateToSaleList = {
                    navController.navigate(NavigationItem.MySales.route)
                }
            )
        }

        composable(NavigationItem.SaveSale.route) {
            SaleScreen(snackbarHostState = snackbarHostState)
        }

        composable(NavigationItem.MySales.route) {
            SaleListScreen(
                snackbarHostState = snackbarHostState,
                navigateToSaleChart = { startDate, endDate ->
                    navController.navigate(NavigationItem.SaleChart.route.plus("/${startDate}/${endDate}"))
                }
            )
        }

        composable(NavigationItem.Product.route) {
            var selectedProduct by remember { mutableStateOf<Product?>(null) }
            var refreshProductList by remember { mutableStateOf(false) }
            var removeSelectedBgColor by remember { mutableStateOf(false) }
            var isRefreshing by remember { mutableStateOf(false) } // For SwipeToRefresh

            LaunchedEffect(isRefreshing) {
                // Show refresh indicator during 1s
                if (isRefreshing) {
                    delay(1000)
                    isRefreshing = false
                }
            }

            PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
                refreshProductList  = !refreshProductList
                isRefreshing = true
            }) {
                Row {
                    Row(Modifier.weight(0.4f)) {
                        ProductListScreen(
                            snackbarHostState = snackbarHostState,
                            refreshProductList = refreshProductList,
                            removeSelectedBgColor = removeSelectedBgColor,
                            onViewDetail = { product ->
                                selectedProduct = product
                            }
                        )
                        VerticalDivider()
                    }

                    Box(Modifier.weight(0.6f)) {
                        ProductDetailScreen(
                            snackbarHostState = snackbarHostState,
                            product = selectedProduct,
                            navigateToCategory = {
                                navController.navigate(NavigationItem.Category.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            },
                            removeSelectedBgColor = {
                                removeSelectedBgColor = !removeSelectedBgColor
                            },
                            refreshProducts = {
                                refreshProductList  = !refreshProductList
                            }
                        )
                    }
                }
            }
        }

        composable(NavigationItem.Category.route) {
            Text("Categories")
        }

        composable(
            route = NavigationItem.SaleChart.route.plus("/{startDate}/{endDate}"),
            arguments = listOf(
                navArgument("startDate") {
                    type = NavType.StringType
                },
                navArgument("endDate") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val startDate = backStackEntry.arguments?.getString("startDate") ?: ""
            val endDate = backStackEntry.arguments?.getString("endDate") ?: ""
            SaleChartScreen(
                startDate = startDate,
                endDate = endDate
            )
        }

        composable(NavigationItem.ForgotPassword.route) {
            ForgotPasswordScreen {
                navController.popBackStack()
            }
        }
    }
}

