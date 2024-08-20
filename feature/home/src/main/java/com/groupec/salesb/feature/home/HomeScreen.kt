package com.groupec.salesb.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    /*val userStoreState by viewModel.configUiState.collectAsState()
    Box {
        when (userStoreState) {
            is ConfigUiState.Loading -> LoadingScreen()
            is ConfigUiState.Configuration -> navigateToConfiguration()
            is ConfigUiState.Login -> navigateToLogin()
            else -> {}
        }
    }*/
}