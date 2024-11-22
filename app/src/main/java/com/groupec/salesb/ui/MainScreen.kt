package com.groupec.salesb.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.SampleTopAppBar
import com.groupec.salesb.navigation.AppNavHost
import com.groupec.salesb.navigation.NavigationItem


@Composable
fun MainScreen(
    connectionState: Boolean,
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel = hiltViewModel()
) {
    // Show title and user name on app bar
    val title = stringResource(id = R.string.app_name)
    val userStoreState by viewModel.userStore.collectAsState()
    val appBarTitle = "${userStoreState.nomprenom} - $title"

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
            dropDownItemsMenu = getDropdownItemsWithActions(navController)
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
            if (currentDestination.value !in listOf(
                    NavigationItem.Loading.route,
                    NavigationItem.Configuration.route,
                    NavigationItem.Login.route
                )
            ) {
                SampleTopAppBar(
                    appBarTitle,
                    onNavigationClick,
                    dropDownItemsMenu
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            AppNavHost(
                modifier = Modifier.padding(it),
                connectionState = connectionState,
                navController = navController
            )
        }
    }
}

fun getDropdownItemsWithActions(navController: NavHostController): List<Pair<String, () -> Unit>> {
    return listOf(
        DropdownItem.Settings.name to { /* navController.executeAction() */ },
    )
}

