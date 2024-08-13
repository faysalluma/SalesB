package com.groupec.feature.configuration

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.LoadingScreenWithInformation

@Composable
internal fun ConfigurationRoute(
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConfigurationViewModel = hiltViewModel(),
) {
    val parameterState by viewModel.parameterUiState.collectAsState()
    ConfigurationScreen(
        parameterState = parameterState,
        navigateToLogin = navigateToLogin,
        modifier = modifier
    )
}


@Composable
internal fun ConfigurationScreen(
    parameterState: ParameterUiState,
    modifier: Modifier = Modifier,
    navigateToLogin: () -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        when (parameterState) {
            is ParameterUiState.Loading -> LoadingScreenWithInformation()
            is ParameterUiState.Success -> navigateToLogin()
            is ParameterUiState.Error -> ErrorScreen(parameterState.message)
        }
    }
}


