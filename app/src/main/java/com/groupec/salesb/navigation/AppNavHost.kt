package com.groupec.salesb.navigation

// import com.facebook.flipper.plugins.navigation.NavigationFlipperPlugin
// import com.groupec.salesb.utils.FlipperNavigationLogger
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.groupec.accountlist.AccountListScreen
import com.groupec.feature.accountdetail.AccountDetailScreen
import com.groupec.feature.categorydetail.CategoryDetailScreen
import com.groupec.feature.categorylist.CategoryListScreen
import com.groupec.feature.configuration.ConfigurationScreen
import com.groupec.feature.forgotpassword.ForgotPasswordScreen
import com.groupec.feature.login.LoginScreen
import com.groupec.feature.outputdetail.OutputDetailScreen
import com.groupec.feature.outputlist.OutputListScreen
import com.groupec.feature.productdetail.ProductDetailScreen
import com.groupec.feature.productlist.ProductListScreen
import com.groupec.feature.rayondetail.RayonDetailScreen
import com.groupec.feature.rayonlist.RayonListScreen
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
import kotlinx.coroutines.delay

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
            if (shouldNotShowInPortraitMode) {
                EmptyScreen(
                    text = stringResource(R.string.error_visible_only_expanded)
                )
            } else {
                SaleScreen(
                    snackbarHostState = snackbarHostState,
                    navigateToProduct = {
                        navController.navigate(NavigationItem.Product.route)
                    }
                )
            }
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

        composable(NavigationItem.Product.route) {
            if (shouldNotShowInPortraitMode) {
                EmptyScreen(
                    text = stringResource(R.string.error_visible_only_expanded)
                )
            } else {
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
                                    navController.navigate(NavigationItem.Category.route)
                                },
                                navigateToRayon = {
                                    navController.navigate(NavigationItem.Rayon.route)
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
        }

        composable(NavigationItem.Category.route) {
            if (shouldNotShowInPortraitMode) {
                EmptyScreen(
                    text = stringResource(R.string.error_visible_only_expanded)
                )
            } else {
                var selectedCategory by remember { mutableStateOf<Category?>(null) }
                var refreshCategoryList by remember { mutableStateOf(false) }
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
                    refreshCategoryList  = !refreshCategoryList
                    isRefreshing = true
                }) {
                    Row {
                        Row(Modifier.weight(0.4f)) {
                            CategoryListScreen(
                                snackbarHostState = snackbarHostState,
                                refreshCategoryList = refreshCategoryList,
                                removeSelectedBgColor = removeSelectedBgColor,
                                onViewDetail = { category ->
                                    selectedCategory = category
                                }
                            )
                            VerticalDivider()
                        }

                        Box(Modifier.weight(0.6f)) {
                            CategoryDetailScreen(
                                snackbarHostState = snackbarHostState,
                                category = selectedCategory,
                                removeSelectedBgColor = {
                                    removeSelectedBgColor = !removeSelectedBgColor
                                },
                                refreshCategories = {
                                    refreshCategoryList  = !refreshCategoryList
                                }
                            )
                        }
                    }
                }
            }
        }

        composable(NavigationItem.Rayon.route) {
            if (shouldNotShowInPortraitMode) {
                EmptyScreen(
                    text = stringResource(R.string.error_visible_only_expanded)
                )
            } else {
                var selectedRayon by remember { mutableStateOf<Rayon?>(null) }
                var refreshList by remember { mutableStateOf(false) }
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
                    refreshList  = !refreshList
                    isRefreshing = true
                }) {
                    Row {
                        Row(Modifier.weight(0.4f)) {
                            RayonListScreen(
                                snackbarHostState = snackbarHostState,
                                refreshList = refreshList,
                                removeSelectedBgColor = removeSelectedBgColor,
                                onViewDetail = { rayon ->
                                    selectedRayon = rayon
                                }
                            )
                            VerticalDivider()
                        }

                        Box(Modifier.weight(0.6f)) {
                            RayonDetailScreen(
                                snackbarHostState = snackbarHostState,
                                rayon = selectedRayon,
                                removeSelectedBgColor = {
                                    removeSelectedBgColor = !removeSelectedBgColor
                                },
                                refreshRayons = {
                                    refreshList  = !refreshList
                                }
                            )
                        }
                    }
                }
            }
        }

        composable(NavigationItem.Outputs.route) {
            if (shouldNotShowInPortraitMode) {
                EmptyScreen(
                    text = stringResource(R.string.error_visible_only_expanded)
                )
            } else {
                var selectedOutput by remember { mutableStateOf<Output?>(null) }
                var refreshList by remember { mutableStateOf(false) }
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
                    refreshList  = !refreshList
                    isRefreshing = true
                }) {
                    Row {
                        Row(Modifier.weight(0.4f)) {
                            OutputListScreen(
                                snackbarHostState = snackbarHostState,
                                refreshList = refreshList,
                                removeSelectedBgColor = removeSelectedBgColor,
                                onViewDetail = { output ->
                                    selectedOutput = output
                                }
                            )
                            VerticalDivider()
                        }

                        Box(Modifier.weight(0.6f)) {
                            OutputDetailScreen(
                                snackbarHostState = snackbarHostState,
                                output = selectedOutput,
                                removeSelectedBgColor = {
                                    removeSelectedBgColor = !removeSelectedBgColor
                                },
                                refreshList = {
                                    refreshList  = !refreshList
                                }
                            )
                        }
                    }
                }
            }
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
            var fromDetail = backStackEntry.arguments?.getBoolean("fromDetail") ?: false
            var fromDetailValue by rememberSaveable { mutableStateOf(fromDetail) }

            LaunchedEffect(isExpandedWidth) {
                if (isExpandedWidth && fromDetailValue) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    account: User? = null,
    onNavigateToDetail: ((User) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null
) {
    var selectedAccount by remember { mutableStateOf<User?>(account) }
    var refreshAccountList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) } // For SwipeToRefresh

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshAccountList = !refreshAccountList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedAccountScreen(
                snackbarHostState = snackbarHostState,
                refreshAccountList = refreshAccountList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { user -> selectedAccount = user },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshAccounts = { refreshAccountList = !refreshAccountList },
                onPopBack = onPopBack
            )
        } else {
            if (onNavigateToDetail != null) {
                AccountListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshAccountList = refreshAccountList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onViewDetail = { user ->
                        onNavigateToDetail(user)
                    }
                )
            } else if (onNavigateToHome != null) {
                AccountDetailScreen(
                    snackbarHostState = snackbarHostState,
                    account = account,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack
                )
            }
        }
    }
}

@Composable
fun ExpandedAccountScreen(
    snackbarHostState: SnackbarHostState,
    refreshAccountList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (User) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshAccounts: () -> Unit,
    onPopBack: (() -> Unit)? = null
) {
    var selectedAccount by remember { mutableStateOf<User?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            AccountListScreen(
                snackbarHostState = snackbarHostState,
                refreshAccountList = refreshAccountList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { user ->
                    selectedAccount = user
                }
            )
        }
        Box(Modifier.weight(0.6f)) {
            AccountDetailScreen(
                snackbarHostState = snackbarHostState,
                account = selectedAccount,
                isExpandedWidth = true,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                refreshAccounts = onRefreshAccounts,
                onPopBack = onPopBack
            )
        }
    }
}


