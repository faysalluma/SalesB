package com.groupec.salesb.ui

import android.content.Context
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.component.CustomSnackBar
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.navigation.AppNavHost
import com.groupec.salesb.navigation.NavigationItem


@Composable
fun MainScreen(
    connectionState: Boolean,
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Show title and user name on app bar
    val userStoreState by viewModel.userStore.collectAsState()
    val userId = userStoreState.id
    val appBarTitle = userStoreState.nomprenom
    val firstLogin = userStoreState.firstLogin
    val resetPassword = userStoreState.reset_password

    // For TopAppBar
    val onNavigationClick: (() -> Unit)? = null
    val dropDownItemsMenu = getDropdownItemsWithActions(context, navController, viewModel, userId, firstLogin)

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
        NavigationItem.Home.route -> {
            viewModel.getUserStore()
            // dropDownItemsMenu = getDropdownItemsWithActions(context, navController, viewModel, userId, firstLogin)
        }

        NavigationItem.ChangePassword.route -> {
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
            if (
                shouldShowBarAndRailApp(
                    route = currentDestination.value,
                    firstLogin = firstLogin,
                    resetPassword = resetPassword
                )
            ) {
                SampleTopAppBar(
                    appBarTitle,
                    onNavigationClick,
                    dropDownItemsMenu
                )
            }
        },
        floatingActionButton = {
            if (
                currentDestination.value == NavigationItem.Home.route
            ) {
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
                    Icon(Icons.Filled.Add, "Add", modifier = Modifier.size(32.dp))
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
                if (shouldShowBarAndRailApp(currentDestination.value, firstLogin, resetPassword)) {
                    MyNavigationRail(navController, modifier = Modifier.weight(0.09f))
                }
                AppNavHost(
                    snackbarHostState = snackbarHostState,
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
                    connectionState = connectionState,
                    navController = navController
                )
            }
        }
    }
}

fun getDropdownItemsWithActions(
    context: Context,
    navController: NavHostController,
    viewModel: MainViewModel,
    userId: String,
    firstLogin: Boolean
): List<MenuItem> {

    return listOf(
        MenuItem.SubMenu(
            context.getString(R.string.menu_settings),
            listOf(
                MenuItem.Action(
                    context.getString(R.string.menu_category)
                ) {
                    navController.navigate(NavigationItem.Category.route)
                },
                MenuItem.Action(
                    context.getString(R.string.menu_rayon)
                ) {
                    navController.navigate(NavigationItem.Rayon.route)
                }
            )
        ),

        MenuItem.Action(
            context.getString(R.string.menu_update_password)
        ) {
            navController.navigate(NavigationItem.ChangePassword.route.plus("/$userId/$firstLogin"))
        },

        MenuItem.Action(
            context.getString(R.string.menu_log_out)
        ) {
            viewModel.logout()
            navController.navigate(NavigationItem.Loading.route) {
                // Delete the entire background stack
                popUpTo(0) { inclusive = true }
            }
        }
    )
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
