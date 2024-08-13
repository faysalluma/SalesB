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
import com.groupec.feature.configuration.navigation.CONFIGURATION_ROUTE
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.SampleTopAppBar
import com.groupec.salesb.navigation.AppNavHost
import com.groupec.feature.login.navigation.LOGIN_ROUTE
import com.groupec.salesb.core.Mode
import com.groupec.salesb.core.designsystem.component.ErrorScreen


@Composable
fun MainScreen(
    connectionState: Boolean, 
    navController: NavHostController = rememberNavController()
) {
    var appBarTitle = stringResource(id = R.string.app_name)
    var onNavigationClick : (() -> Unit) ? = null
    var dropDownItemsMenu: List<Pair<String, () -> Unit>>  = emptyList()

    val currentDestination = remember {
        mutableStateOf(navController.currentDestination?.route)
    }

    LaunchedEffect(navController) {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            currentDestination.value = destination.route?.substringBeforeLast("/")?.substringBeforeLast("?")
        }
    }

    when (currentDestination.value) {
        LOGIN_ROUTE -> {
            dropDownItemsMenu = getDropdownItemsWithActions(navController)
        }
        else -> {
            // Handle unexpected destinations (optional)
        }
    }

    Scaffold(
        topBar = {
            if (currentDestination.value !in listOf(CONFIGURATION_ROUTE, LOGIN_ROUTE)) {
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

