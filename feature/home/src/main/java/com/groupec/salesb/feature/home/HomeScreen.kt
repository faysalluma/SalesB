package com.groupec.salesb.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.LoadingScreen


@Composable
internal fun HomeRoute(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val userStoreState by viewModel.configUiState.collectAsState()
    HomeScreen(
        navigateToConfiguration = navigateToConfiguration,
        navigateToLogin = navigateToLogin,
        userStoreState = userStoreState,
        modifier = modifier
    )
}


@Composable
internal fun HomeScreen(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: () -> Unit,
    userStoreState: ConfigUiState,
    modifier: Modifier = Modifier
) {
    Box {
        when (userStoreState) {
            is ConfigUiState.Loading -> LoadingScreen()
            is ConfigUiState.Configuration -> navigateToConfiguration()
            is ConfigUiState.Login -> navigateToLogin()
            else -> {}
        }
    }
}