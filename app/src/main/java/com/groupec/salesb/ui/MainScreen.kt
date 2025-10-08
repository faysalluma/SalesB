package com.groupec.salesb.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.Privileges
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
import com.groupec.salesb.navigation.AppNavHost
import com.groupec.salesb.navigation.NavigationItem


@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen(
    connectionState: Boolean,
    isExpandedWidth: Boolean,
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show title and user name on app bar
    val userStoreState by viewModel.userStore.collectAsState()
    val appBarTitle = userStoreState.nomprenom
    val firstLogin = userStoreState.firstLogin
    val resetPassword = userStoreState.reset_password
    val privileges = userStoreState.getPrivileges()
    val logoutState by viewModel.logoutUiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // For TopAppBar
    val onNavigationClick: (() -> Unit)? = null
    val dropDownItemsMenu = getDropdownItemsWithActions(context, navController, userStoreState) {
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
                    popUpTo(0) { inclusive = true }
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

    val items by remember(privileges) {
        derivedStateOf { getNavigationItemsList(privileges)  }
    }

    val shouldNotShowInPortraitMode by remember {
        derivedStateOf { !isExpandedWidth && !isPortaitScreenActive(currentDestination.value) }
    }

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
                    SampleTopAppBar(
                        appBarTitle,
                        onNavigationClick,
                        dropDownItemsMenu
                    )
                }
            }
        },
        bottomBar = {
            currentDestination.value?.let { route ->
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
                        onItemClick = { currentNavigationItem ->
                            navController.navigate(currentNavigationItem.route) {
                                popUpTo(navController.graph.startDestinationRoute ?: "") {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            if (connectionState && currentDestination.value == NavigationItem.Home.route) {
                LargeFloatingActionButton(
                    onClick = {
                        navController.navigate(NavigationItem.SaveSale.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    shape = CircleShape,
                    containerColor = Primary,
                    contentColor = White,
                ) {
                    Icon(Icons.Filled.Add, "Add", modifier = Modifier.size(30.dp))
                }
            }
        }
    ) {
        Row(modifier = Modifier.padding(it)) {
            if (!connectionState && shouldShowBarAndRailApp(currentDestination.value, firstLogin, resetPassword)) {
                ErrorScreen(
                    error = stringResource(R.string.no_internet_connexion)
                )
            } else {
                val startDestination by remember(items) {
                    derivedStateOf {
                        when {
                            items.isEmpty() || (items.isNotEmpty() && firstLogin) || (items.isNotEmpty() && resetPassword.isNotEmpty())
                                 -> NavigationItem.Loading.route
                            else -> items.first().route
                        }
                    }
                }

                if (
                    shouldShowBarAndRailApp(currentDestination.value, firstLogin, resetPassword) &&
                    isExpandedWidth
                ) {
                    MyNavigationRail(
                        items = items,
                        navController,
                        modifier = Modifier.weight(0.09f)
                    )
                }

                AppNavHost(
                    snackbarHostState = snackbarHostState,
                    isExpandedWidth = isExpandedWidth,
                    shouldNotShowInPortraitMode = shouldNotShowInPortraitMode,
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
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}

private fun getNavigationItemsList(privileges: List<String>): List<NavigationItem> {
    val cudPrivileges = listOf(
        Approval.AUTHORIZE_ADD,
        Approval.AUTHORIZE_EDIT,
        Approval.AUTHORIZE_DELETE
    )

    val mappings = listOf(
        Privileges.Home.getKeysByApprovals(
            listOf(
                Approval.STAT_PERIODIC,
                Approval.STAT_NON_PERIODIC,
                Approval.STAT_CHART,
            )
        ) to NavigationItem.Home,

        Privileges.Sale.getKeysByApprovals(listOf(Approval.AUTHORIZE_VIEW)) to NavigationItem.SaveSale,

        Privileges.MySales.getKeysByApprovals(cudPrivileges) to NavigationItem.MySales,

        Privileges.Product.getKeysByApprovals(cudPrivileges) to NavigationItem.Product,

        Privileges.Outputs.getKeysByApprovals(cudPrivileges) to NavigationItem.Outputs
    )

    return mappings
        .filter { (requiredKeys, _) -> privileges.any { it in requiredKeys } }
        .map { it.second }
}

fun getDropdownItemsWithActions(
    context: Context,
    navController: NavHostController,
    userStore: UserStore,
    onLogOut: () -> Unit
): List<MenuItem> {

    val privileges = userStore.getPrivileges()
    val items = mutableListOf<MenuItem>()
    val cudPrivileges = listOf(Approval.AUTHORIZE_ADD, Approval.AUTHORIZE_EDIT, Approval.AUTHORIZE_DELETE)

    // Add Parameters items
    val hasCategoryPrivilege = privileges.any { it in Privileges.Category.getKeysByApprovals(cudPrivileges) }
    val hasRayonPrivilege =  privileges.any { it in Privileges.Rayon.getKeysByApprovals(cudPrivileges) }
    val hasUserSettingsPrivilege =  privileges.any { it in Privileges.UserSettings.getKeysByApprovals(
        listOf(Approval.AUTHORIZE_VIEW)
    ) }

    if (hasCategoryPrivilege || hasRayonPrivilege) {
        val childrenList = mutableListOf<MenuItem.Action>()
        if (hasCategoryPrivilege) {
            childrenList.add(
                MenuItem.Action(
                    context.getString(R.string.menu_category)
                ) {
                    navController.navigate(NavigationItem.Category.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        if (hasRayonPrivilege) {
            childrenList.add(
                MenuItem.Action(
                    context.getString(R.string.menu_rayon)
                ) {
                    navController.navigate(NavigationItem.Rayon.route) {
                        launchSingleTop = true
                    }
                }
            )
        }

        items.add(
            MenuItem.SubMenu(
                context.getString(R.string.menu_settings),
                childrenList
            )
        )
    }

    // Add UserManagement Account item
    if (hasUserSettingsPrivilege) {
        items.add(
            MenuItem.Action(
                context.getString(R.string.manage_your_account)
            ) {
                navController.navigate(NavigationItem.Account.route) {
                    launchSingleTop = true
                }
            }
        )
    }

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
        NavigationItem.ForgotPassword.route
    )

    if (firstLogin || resetPassword.isNotEmpty()) {
        excludedRoutes.add(
            NavigationItem.ChangePassword.route
        )
    }

    return route !in excludedRoutes
}

private fun isPortaitScreenActive(route: String?): Boolean {
    val excludedRoutes =  mutableListOf(
        NavigationItem.Loading.route,
        NavigationItem.Configuration.route,
        NavigationItem.Login.route,
        NavigationItem.ForgotPassword.route,
        NavigationItem.ChangePassword.route,
        NavigationItem.Home.route,
        NavigationItem.MySales.route,
        NavigationItem.Account.route,
    )
    return route in excludedRoutes
}
