package com.groupec.salesb.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.SampleTopAppBar
import com.groupec.salesb.core.designsystem.theme.Primary
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

    // Show title and user name on app bar
    val userStoreState by viewModel.userStore.collectAsState()
    val appBarTitle = userStoreState.nomprenom

    var onNavigationClick: (() -> Unit)? = null
    var dropDownItemsMenu: List<Pair<String, () -> Unit>> = emptyList()

    val currentDestination = remember {
        mutableStateOf(navController.currentDestination?.route)
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            currentDestination.value =
                destination.route?.substringBeforeLast("/")?.substringBeforeLast("?")
        }
    }

    when (currentDestination.value) {
        NavigationItem.Home.route -> {
            dropDownItemsMenu = getDropdownItemsWithActions(context, navController)
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
        topBar = {
            if (shouldShowBarAndRailApp(currentDestination.value)) {
                SampleTopAppBar(
                    appBarTitle,
                    onNavigationClick,
                    dropDownItemsMenu
                )
            }
        },
        floatingActionButton = {
            LargeFloatingActionButton(
                onClick = {
                    navController.navigate(NavigationItem.Sale.route) {
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
    ) {
        Row(modifier = Modifier.padding(it)) {
            if (shouldShowBarAndRailApp(currentDestination.value)) {
                MyNavigationRail(navController, modifier = Modifier.weight(0.09f))
            }
            AppNavHost(
                modifier = Modifier
                    .weight(if (shouldShowBarAndRailApp(currentDestination.value)) 0.91f else 1f)
                    .padding(16.dp),
                connectionState = connectionState,
                navController = navController
            )
        }
    }
}

fun getDropdownItemsWithActions(context: Context, navController: NavHostController): List<Pair<String, () -> Unit>> {
    return listOf(
        context.getString(R.string.menu_settings) to { /* navController.executeAction() */ },
        context.getString(R.string.menu_update_password) to { /* navController.executeAction() */ },
        context.getString(R.string.menu_log_out) to { /* navController.executeAction() */ },
    )
}

private fun shouldShowBarAndRailApp(route: String?): Boolean {
    return route !in listOf(
        NavigationItem.Loading.route,
        NavigationItem.Configuration.route,
        NavigationItem.Login.route
    )
}
