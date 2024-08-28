package com.groupec.salesb.feature.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen


@Composable
fun LoadingScreen(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: (String) -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoadingViewModel = hiltViewModel(),
) {
    val userStoreState by viewModel.configUiState.collectAsState()
    Box {
        when (userStoreState) {
            is ConfigUiState.Loading -> AppLoadingScreen()
            is ConfigUiState.Configuration -> {
                LaunchedEffect(Unit) {
                    navigateToConfiguration()
                }
            }
            is ConfigUiState.Login -> {
                LaunchedEffect(Unit) {
                    navigateToLogin((userStoreState as ConfigUiState.Login).raisonSociale)
                }
            }
            is ConfigUiState.Home -> {
                LaunchedEffect(Unit) {
                    navigateToHome()
                }
            }
        }
    }
}