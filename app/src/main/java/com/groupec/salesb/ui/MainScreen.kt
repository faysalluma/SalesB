package com.groupec.salesb.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.CustomSnackBar
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.OfflineErrorScreen
import com.groupec.salesb.navigation.AppNavHost
import com.groupec.salesb.navigation.NavigationItem


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen(
    connectionState: Boolean,
    isExpandedWidth: Boolean,
    isTablet: Boolean,
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show title and user name on app bar
    val userStoreState by viewModel.userStore.collectAsState()
    val parameterState by viewModel.parameter.collectAsState()
    val appBarTitle = userStoreState.nomprenom
    val firstLogin = userStoreState.firstLogin
    val resetPassword = userStoreState.reset_password
    val privileges = userStoreState.getPrivileges()
    val logoutState by viewModel.logoutUiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // For TopAppBar
    val dropDownItemsMenu = getDropdownItemsWithActions(
        context = context,
        navController = navController,
        userStore = userStoreState,
        isServiceView = parameterState.serviceview
    ) {
        showLogoutDialog = true
    }

    if (showLogoutDialog) {
        AppAlertInfoDialog(
            setShowDialog = {
                showLogoutDialog = it
            },
            title = stringResource(R.string.confirm_log_out),
            onConfirmButton = {
                viewModel.logout()
            },
            onDismissButton = {}
        )
    }

    when (logoutState) {
        is UIState.Success -> {
            LaunchedEffect(Unit) {
                viewModel.getUserStore()
                navController.navigate(NavigationItem.Loading.route) {
                    // Delete the entire background stack
                    popUpTo(0) { inclusive = true } // la pile est complètement vidée, donc l’écran actuel devient le seul dans la stack.
                }
                viewModel.resetFlow()
            }
        }
        is UIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(logoutState as UIState.Error).message,
                        isError = true
                    )
                )
            }
        }
        else -> {}
    }

    val currentDestination = remember { mutableStateOf(navController.currentDestination?.route) }
    DisposableEffect(navController) {
        val listener = NavController.OnDestinationChangedListener { _, destination, _ ->
            currentDestination.value =
                destination.route?.substringBefore("/")?.substringBefore("?")
        }
        navController.addOnDestinationChangedListener(listener)
        onDispose {
            navController.removeOnDestinationChangedListener(listener)
        }
    }

    when (currentDestination.value) {
        NavigationItem.Home.route, NavigationItem.ChangePassword.route -> {
            viewModel.getUserStore()
        }

        /*  NavigationItem.Detail.route -> {
              // Get arguments and show it in TopBar
              val order = navController.previousBackStackEntry?.savedStateHandle?.get<Order>("order")
              appBarTitle = "Order ${order?.id}"

              // Definie onNavigationClick method
              onNavigationClick = {
                  navController.popBackStack()
              }
          }*/

        else -> {
            // Handle unexpected destinations (optional)
        }
    }

    val items by remember {
        mutableStateOf(
            listOf(
                NavigationItem.Home,
                NavigationItem.SaveSale,
                NavigationItem.MySales,
                NavigationItem.Product,
                NavigationItem.Outputs
            )
        )
    }

    // For internet error screen
    val retryOfflineAction: () -> Unit = {
        viewModel.getUserStore()
        viewModel.getParameterStore()
        if (connectionState && currentDestination.value != NavigationItem.Login.route) {
            viewModel.checkSubscriptionExpiration()
        }
    }

    val openNetworkSettingsAction: () -> Unit = {
        val settingsIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Intent(Settings.Panel.ACTION_INTERNET_CONNECTIVITY)
        } else {
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        try {
            context.startActivity(settingsIntent)
        } catch (_: Exception) {
            context.startActivity(
                Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    // Log out customers when subscription expire
    ComposableLifecycle(
        onResume = {
            viewModel.getParameterStore()
            if (currentDestination.value!= null && currentDestination.value != NavigationItem.Login.route) {
                viewModel.checkSubscriptionExpiration()
            }
        }
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                snackbar = { data ->
                    val isError = (data.visuals as? SnackbarVisualsWithState)?.isError ?: false
                    val containerColor = if (isError) Red else Green

                    CustomSnackBar(
                        data = data,
                        containerColor = containerColor,
                        contentColor = White
                    )
                }
            )
        },
        topBar = {
            currentDestination.value?.let { route ->
                if (shouldShowBarAndRailApp(route, firstLogin, resetPassword)) {
                    val titleSignup =  if (route == NavigationItem.Signup.route) {
                        stringResource(com.groupec.feature.signup.R.string.signup_title)
                    } else null
                    SampleTopAppBar(
                        titleSignup ?: appBarTitle,
                        dropDownItemsMenu = if (route != NavigationItem.Signup.route) dropDownItemsMenu else emptyList()
                    )
                }
            }
        },
        bottomBar = {
            currentDestination.value?.let { route ->
                if (shouldShowBarAndRailApp(route, firstLogin, resetPassword)) {
                    // Don't show BottomBar for signup screen
                    if (route != NavigationItem.Signup.route) {
                        AnimatedVisibility(
                            visible = !isExpandedWidth,
                            enter = slideInVertically(
                                // Slide in from the bottom
                                initialOffsetY = { fullHeight -> fullHeight }
                            ),
                            exit = slideOutVertically(
                                // Slide out to the bottom
                                targetOffsetY = { fullHeight -> fullHeight }
                            )
                        ) {
                            BottomNavigationBar(
                                items = items,
                                currentRoute = route,
                                isServiceView = parameterState.serviceview,
                                onItemClick = { currentNavigationItem ->
                                    navController.navigate(currentNavigationItem.route) {
                                        // Supprime toutes les destinations jusqu’à la destination de départ du graphe de navigation
                                        popUpTo(navController.graph.startDestinationRoute ?: "") {
                                            // saveState = true (A utiliser dans le cas ou les ecrans des items menus
                                            // se trouvent dans le même graphe de navigation
                                        }
                                        launchSingleTop = true
                                        // restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (connectionState &&
                (
                   currentDestination.value == NavigationItem.Home.route ||
                   (currentDestination.value == NavigationItem.Account.route && !isExpandedWidth) ||
                   (currentDestination.value == NavigationItem.Outputs.route && !isExpandedWidth) ||
                   (currentDestination.value == NavigationItem.Category.route && !isExpandedWidth) ||
                   (currentDestination.value == NavigationItem.Rayon.route && !isExpandedWidth) ||
                   (currentDestination.value == NavigationItem.Product.route && !isExpandedWidth)
                )
            ) {
                val onFabClick = {
                    when (currentDestination.value) {
                        NavigationItem.Home.route -> navController.navigate(NavigationItem.SaveSale.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                        NavigationItem.Account.route -> navController.navigate(NavigationItem.AccountDetail.route)
                        NavigationItem.Outputs.route -> navController.navigate(NavigationItem.OutputDetail.route)
                        NavigationItem.Product.route -> navController.navigate(NavigationItem.ProductDetail.route)
                        NavigationItem.Category.route -> navController.navigate(NavigationItem.CategoryDetail.route)
                        NavigationItem.Rayon.route -> navController.navigate(NavigationItem.RayonDetail.route)
                    }
                }

                val fabShape = CircleShape
                val fabContainerColor = Primary
                val fabContentColor = White

                if (isTablet) {
                    LargeFloatingActionButton(
                        onClick = onFabClick,
                        shape = fabShape,
                        containerColor = fabContainerColor,
                        contentColor = fabContentColor,
                    ) {
                        Icon(Icons.Filled.Add, "Add", modifier = Modifier.size(32.dp))
                    }
                } else {
                    FloatingActionButton(
                        onClick = onFabClick,
                        shape = fabShape,
                        containerColor = fabContainerColor,
                        contentColor = fabContentColor,
                    ) {
                        Icon(Icons.Filled.Add, "Add")
                    }
                }
            }
        }
    ) {
        Row(modifier = Modifier.padding(it)) {
            if (!connectionState && shouldShowBarAndRailApp(currentDestination.value, firstLogin, resetPassword)) {
                OfflineErrorScreen(
                    onRetry = retryOfflineAction,
                    onOpenNetworkSettings = openNetworkSettingsAction
                )
            } else {
                if (
                    shouldShowBarAndRailApp(currentDestination.value, firstLogin, resetPassword) &&
                    isExpandedWidth
                ) {
                    // Don't show NavRail for signup screen
                    if (currentDestination.value != NavigationItem.Signup.route) {
                        MyNavigationRail(
                            items = items,
                            navController,
                            isServiceView = parameterState.serviceview,
                            modifier = Modifier.weight(0.09f)
                        )
                    }
                }

                AppNavHost(
                    snackbarHostState = snackbarHostState,
                    isExpandedWidth = isExpandedWidth,
                    modifier = Modifier
                        .weight(
                            if (shouldShowBarAndRailApp(
                                    currentDestination.value,
                                    firstLogin,
                                    resetPassword
                                )
                            ) 0.91f else 1f
                        )
                        .padding(16.dp),
                    navController = navController
                )
            }
        }
    }
}

fun getDropdownItemsWithActions(
    context: Context,
    navController: NavHostController,
    userStore: UserStore,
    isServiceView: Boolean,
    onLogOut: () -> Unit
): List<MenuItem> {

    val items = mutableListOf<MenuItem>()

    // Add Parameters items
    val childrenList = mutableListOf<MenuItem.Action>()
    childrenList.add(
        MenuItem.Action(
            context.getString(R.string.menu_category)
        ) {
            navController.navigate(NavigationItem.Category.route) {
                launchSingleTop = true
            }
        }
    )

    childrenList.add(
        MenuItem.Action(
            context.getString(R.string.menu_handle_service)
        ) {
            navController.navigate(NavigationItem.HandleService.route) {
                launchSingleTop = true
            }
        }
    )

    /*childrenList.add(
        MenuItem.Action(
            context.getString(R.string.menu_rayon)
        ) {
            navController.navigate(NavigationItem.Rayon.route) {
                launchSingleTop = true
            }
        }
    )*/

    items.add(
        MenuItem.SubMenu(
            context.getString(R.string.menu_settings),
            childrenList
        )
    )

    items.add(
        MenuItem.Action(
            context.getString(R.string.menu_print_receipt_guide)
        ) {
            navController.navigate(NavigationItem.PrintReceiptGuide.route) {
                launchSingleTop = true
            }
        }
    )

    // Add  remaining list
    items.addAll(
        listOf(
            MenuItem.Action(
                context.getString(R.string.menu_update_password)
            ) {
                navController.navigate(NavigationItem.ChangePassword.route.plus(
                    "/${userStore.id}/${userStore.firstLogin}"
                )) {
                    launchSingleTop = true
                }
            },

            MenuItem.Action(
                label = context.getString(R.string.menu_log_out),
                onClick = onLogOut
            )
        )
    )

    return items
}

private fun shouldShowBarAndRailApp(
    route: String?,
    firstLogin: Boolean = false,
    resetPassword: String
): Boolean {
    val excludedRoutes =  mutableListOf(
        NavigationItem.Loading.route,
        NavigationItem.Configuration.route,
        NavigationItem.Login.route,
        NavigationItem.ForgotPassword.route,
        NavigationItem.TermsAndConditions.route
    )

    if (firstLogin || resetPassword.isNotEmpty()) {
        excludedRoutes.add(
            NavigationItem.ChangePassword.route
        )
    }

    return route !in excludedRoutes
}
