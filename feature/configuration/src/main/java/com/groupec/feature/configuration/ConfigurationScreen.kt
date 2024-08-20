package com.groupec.feature.configuration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.AppLoadingScreenWithInformation
import com.groupec.salesb.core.designsystem.component.ErrorScreen


@Composable
fun ConfigurationScreen(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val parameterState by viewModel.parameterUiState.collectAsState()

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (parameterState) {
            is ParameterUiState.Loading -> AppLoadingScreenWithInformation()
            is ParameterUiState.Success -> {
                LaunchedEffect(Unit) {
                    navigateToLogin()
                }
            }
            is ParameterUiState.Error -> ErrorScreen((parameterState as ParameterUiState.Error).message)
        }
    }
}


