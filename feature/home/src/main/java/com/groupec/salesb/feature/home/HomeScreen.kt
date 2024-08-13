package com.groupec.salesb.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.designsystem.component.LoadingScreen
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore


@Composable
internal fun HomeRoute(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val parameterState by viewModel.parameterUiState.collectAsState()
    val userStoreState by viewModel.userUiState.collectAsState()
    HomeScreen(
        navigateToConfiguration = navigateToConfiguration,
        navigateToLogin = navigateToLogin,
        parameterState = parameterState,
        userStoreState = userStoreState,
        modifier = modifier
    )
}


@Composable
internal fun HomeScreen(
    navigateToConfiguration: () -> Unit,
    navigateToLogin: () -> Unit,
    parameterState: ParameterUiState,
    userStoreState: UserUiState,
    modifier: Modifier = Modifier
) {
    Box {
        when (parameterState) {
            is ParameterUiState.Loading -> LoadingScreen()
            is ParameterUiState.Success -> {
                // Check if parameter one value exists (here raisonsociale)
                val raisonSoc = parameterState.paremeter.raisonsociale
                if (raisonSoc.isEmpty()) {
                    navigateToConfiguration()
                } else {
                    when (userStoreState) {
                        is UserUiState.Loading -> {}
                        is UserUiState.Success -> {
                            // Check if userStore one value exists (here id)
                            val userId = userStoreState.userStore.id
                            if (userId.isEmpty()) {
                                navigateToLogin()
                            } else {
                               //
                            }
                        }
                    }
                }
            }
        }
    }
}