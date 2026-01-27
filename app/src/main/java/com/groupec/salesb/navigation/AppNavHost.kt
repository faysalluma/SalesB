package com.groupec.salesb.navigation

// import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
// import com.groupec.salesb.utils.FlipperNavigationLogger
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.groupec.feature.configuration.ConfigurationScreen
import com.groupec.feature.forgotpassword.ForgotPasswordScreen
import com.groupec.feature.login.LoginScreen
import com.groupec.feature.sale.SaleScreen
import com.groupec.feature.salechart.SaleChartScreen
import com.groupec.feature.salelist.SaleListScreen
import com.groupec.feature.termsandconditions.TermsAndConditionsScreen
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.feature.changepassword.ChangePasswordScreen
import com.groupec.salesb.feature.home.HomeScreen
import com.groupec.salesb.feature.loading.LoadingScreen
import com.groupec.salesb.ui.customlistdetailpane.AccountNavContent
import com.groupec.salesb.ui.customlistdetailpane.CategoryNavContent
import com.groupec.salesb.ui.customlistdetailpane.OutputNavContent
import com.groupec.salesb.ui.customlistdetailpane.ProductNavContent
import com.groupec.salesb.ui.customlistdetailpane.RayonNavContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(
    modifier: Modifier,
    snackbarHostState: SnackbarHostState,
    isExpandedWidth: Boolean,
    shouldNotShowInPortraitMode: Boolean,
    navController: NavHostController,
    startDestination: String
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

        // Create this screen loading component

        composable(NavigationItem.Loading.route) {
            LoadingScreen(
                navigateToTermsAndCoditions = {
                    navController.navigate(NavigationItem.TermsAndConditions.route) {
                        popUpTo(NavigationItem.Loading.route) { inclusive = true }
                    }
                },
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

        composable(NavigationItem.TermsAndConditions.route) {
            TermsAndConditionsScreen(
                navigateToConfiguration = {
                    navController.navigate(NavigationItem.Configuration.route) {
                        popUpTo(NavigationItem.TermsAndConditions.route) { inclusive = true }
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
                isExpandedWidth = isExpandedWidth,
                raisonSociale = raisonSociale,
                navigateToChangePassword = { userId, firstLoginOrResetPwd ->
                    navController.navigate(NavigationItem.ChangePassword.route.plus("/${userId}/${firstLoginOrResetPwd}"))
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
            route = NavigationItem.ChangePassword.route.plus("/{userId}/{firstLoginOrResetPwd}"),
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.IntType
                },
                navArgument("firstLoginOrResetPwd") {
                    type = NavType.BoolType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: 0
            val firstLoginOrResetPwd = backStackEntry.arguments?.getBoolean("firstLoginOrResetPwd") ?: false
            ChangePasswordScreen(
                userId = userId,
                firstLoginOrResetPwd = firstLoginOrResetPwd,
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
                },
                navigateToProduct = {
                    navController.navigate(NavigationItem.Product.route)
                }
            )
        }

        composable(NavigationItem.SaveSale.route) {
            SaleScreen(
                snackbarHostState = snackbarHostState,
                isExpandedWidth = isExpandedWidth,
                navigateToProduct = {
                    navController.navigate(NavigationItem.Product.route)
                }
            )
        }

        composable(NavigationItem.MySales.route) {
            SaleListScreen(
                snackbarHostState = snackbarHostState,
                isExpandedWidth = isExpandedWidth,
                navigateToSaleChart = { startDate, endDate ->
                    navController.navigate(NavigationItem.SaleChart.route.plus("/${startDate}/${endDate}"))
                }
            )
        }

        composable(
            route = NavigationItem.Product.route.plus("?fromDetail={fromDetail}"),
            arguments = listOf(
                navArgument("fromDetail") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                if (fromDetailValue) {
                    fromDetailValue = false
                }
            }

            ProductNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                fromDetail = fromDetailValue,
                onNavigateToDetail = { product ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("product", product)
                    navController.navigate(NavigationItem.ProductDetail.route)
                },
                onPopBack = {
                    navController.navigate(startDestination) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                onNavigateToCategory = {
                    navController.navigate(NavigationItem.Category.route)
                },
                onNavigateToRayon = {
                    navController.navigate(NavigationItem.Rayon.route)
                }
            )
        }

        composable(NavigationItem.ProductDetail.route) {
            val product = navController.previousBackStackEntry?.savedStateHandle?.get<Product>("product")

            ProductNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                product = product,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Product.route.plus("?fromDetail=true")) {
                        popUpTo(NavigationItem.Product.route) { inclusive = true }
                    }
                },
                onPopBack = {
                    navController.navigate(NavigationItem.Product.route.plus("?fromDetail=false")) {
                        popUpTo(NavigationItem.Product.route) { inclusive = true }
                    }
                },
                onNavigateToCategory = {
                    navController.navigate(NavigationItem.Category.route)
                },
                onNavigateToRayon = {
                    navController.navigate(NavigationItem.Rayon.route)
                }
            )
        }

        composable(
            route = NavigationItem.Category.route.plus("?fromDetail={fromDetail}"),
            arguments = listOf(
                navArgument("fromDetail") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                if (fromDetailValue) {
                    fromDetailValue = false
                }
            }

            CategoryNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                fromDetail = fromDetailValue,
                onNavigateToDetail = { category ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("category", category)
                    navController.navigate(NavigationItem.CategoryDetail.route)
                },
                onPopBack = {
                    navController.navigate(startDestination) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavigationItem.CategoryDetail.route) {
            val category = navController.previousBackStackEntry?.savedStateHandle?.get<Category>("category")

            CategoryNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                category = category,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Category.route.plus("?fromDetail=true")) {
                        popUpTo(NavigationItem.Category.route) { inclusive = true }
                    }
                },
                onPopBack = {
                    navController.navigate(NavigationItem.Category.route.plus("?fromDetail=false")) {
                        popUpTo(NavigationItem.Category.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationItem.Rayon.route.plus("?fromDetail={fromDetail}"),
            arguments = listOf(
                navArgument("fromDetail") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                if (fromDetailValue) {
                    fromDetailValue = false
                }
            }

            RayonNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                fromDetail = fromDetailValue,
                onNavigateToDetail = { rayon ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("rayon", rayon)
                    navController.navigate(NavigationItem.RayonDetail.route)
                },
                onPopBack = {
                    navController.navigate(startDestination) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavigationItem.RayonDetail.route) {
            val rayon = navController.previousBackStackEntry?.savedStateHandle?.get<Rayon>("rayon")

            RayonNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                rayon = rayon,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Rayon.route.plus("?fromDetail=true")) {
                        popUpTo(NavigationItem.Rayon.route) { inclusive = true }
                    }
                },
                onPopBack = {
                    navController.navigate(NavigationItem.Rayon.route.plus("?fromDetail=false")) {
                        popUpTo(NavigationItem.Rayon.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = NavigationItem.Outputs.route.plus("?fromDetail={fromDetail}"),
            arguments = listOf(
                navArgument("fromDetail") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                if (fromDetailValue) {
                    fromDetailValue = false
                }
            }

            OutputNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                fromDetail = fromDetailValue,
                onNavigateToDetail = { output ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("output", output)
                    navController.navigate(NavigationItem.OutputDetail.route)
                },
                onPopBack = {
                    navController.navigate(startDestination) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavigationItem.OutputDetail.route) {
            val output = navController.previousBackStackEntry?.savedStateHandle?.get<Output>("output")

            OutputNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                output = output,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Outputs.route.plus("?fromDetail=true")) {
                        popUpTo(NavigationItem.Outputs.route) { inclusive = true }
                    }
                },
                onPopBack = {
                    navController.navigate(NavigationItem.Outputs.route.plus("?fromDetail=false")) {
                        popUpTo(NavigationItem.Outputs.route) { inclusive = true }
                    }
                }
            )
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

        composable(
            route = NavigationItem.Account.route.plus("?fromDetail={fromDetail}"),
            arguments = listOf(
                navArgument("fromDetail") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }
            val configuration = LocalConfiguration.current
            LaunchedEffect(configuration) {
                if (fromDetailValue) {
                    fromDetailValue = false
                }
            }

            AccountNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                fromDetail = fromDetailValue,
                onNavigateToDetail = { user ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("account", user)
                    navController.navigate(NavigationItem.AccountDetail.route)
                },
                onPopBack = {
                    navController.navigate(startDestination) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(NavigationItem.AccountDetail.route) {
            val account = navController.previousBackStackEntry?.savedStateHandle?.get<User>("account")

            AccountNavContent(
                isExpandedWidth = isExpandedWidth,
                snackbarHostState = snackbarHostState,
                account = account,
                onNavigateToHome = {
                    navController.navigate(NavigationItem.Account.route.plus("?fromDetail=true")) {
                        popUpTo(NavigationItem.Account.route) { inclusive = true }
                    }
                },
                onPopBack = {
                    navController.navigate(NavigationItem.Account.route.plus("?fromDetail=false")) {
                        popUpTo(NavigationItem.Account.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
