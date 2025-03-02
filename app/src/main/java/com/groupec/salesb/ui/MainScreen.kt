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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.SampleTopAppBar
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
    val scope = rememberCoroutineScope()

    // Show title and user name on app bar
    val userStoreState by viewModel.userStore.collectAsState()
    val userId = userStoreState.id
    val appBarTitle = userStoreState.nomprenom
    val firstLogin = userStoreState.firstLogin

    // For TopAppBar
    var onNavigationClick: (() -> Unit)? = null
    var dropDownItemsMenu: List<Pair<String, () -> Unit>> = emptyList()

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
            dropDownItemsMenu = getDropdownItemsWithActions(context, navController, viewModel, userId, firstLogin)
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
                    firstLogin = firstLogin
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
            if (!connectionState && shouldShowBarAndRailApp(currentDestination.value, firstLogin)) {
                ErrorScreen(
                    error = stringResource(R.string.no_internet_connexion)
                )
            } else {
                if (shouldShowBarAndRailApp(currentDestination.value, firstLogin)) {
                    MyNavigationRail(navController, modifier = Modifier.weight(0.09f))
                }
                AppNavHost(
                    snackbarHostState = snackbarHostState,
                    modifier = Modifier
                        .weight(
                            if (shouldShowBarAndRailApp(
                                    currentDestination.value,
                                    firstLogin
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
): List<Pair<String, () -> Unit>> {

    return listOf(
        context.getString(R.string.menu_settings) to { /* navController.executeAction() */ },
        context.getString(R.string.menu_update_password) to {
            navController.navigate(NavigationItem.ChangePassword.route.plus("/$userId/$firstLogin"))
        },
        context.getString(R.string.menu_log_out) to {
            viewModel.logout()
            navController.navigate(NavigationItem.Loading.route) {
                // Delete the entire background stack
                popUpTo(0) { inclusive = true }
            }
        },
    )
}

private fun shouldShowBarAndRailApp(route: String?, firstLogin: Boolean = false): Boolean {
    val excludedRoutes = if (firstLogin) {
        listOf(
            NavigationItem.Loading.route,
            NavigationItem.Configuration.route,
            NavigationItem.Login.route,
            NavigationItem.ChangePassword.route
        )
    } else {
        listOf(
            NavigationItem.Loading.route,
            NavigationItem.Configuration.route,
            NavigationItem.Login.route
        )
    }
    return route !in excludedRoutes
}
