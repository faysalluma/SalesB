package com.groupec.feature.signup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import com.groupec.salesb.core.domain.signup.SaveSignupConfigurationUseCase
import com.groupec.salesb.core.domain.user.LoginUseCase
import com.groupec.salesb.core.model.data.SignupConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val saveSignupConfigurationUseCase: SaveSignupConfigurationUseCase,
    private val saveParameterUseCase: SaveParameterUseCase,
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _signupConfigurationUiState = MutableStateFlow<SignupConfigurationUiState>(SignupConfigurationUiState.Idle)
    val signupConfigurationUiState: StateFlow<SignupConfigurationUiState> = _signupConfigurationUiState.asStateFlow()

    fun saveInitialConfiguration(configuration: SignupConfiguration, uriLogo: Uri?) {
        viewModelScope.launch {
            _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
            when (val saved = saveSignupConfigurationUseCase(configuration, uriLogo)) {
                is Result.Success -> when (val login = loginUseCase(configuration.email, configuration.password)) {
                    is Result.Success -> when (val parameter = saveParameterUseCase(saved.data)) {
                        is Result.Success -> _signupConfigurationUiState.value = SignupConfigurationUiState.Success
                        is Result.Error -> _signupConfigurationUiState.value = SignupConfigurationUiState.Error(parameter.exception.message ?: "Unknown error")
                        is Result.Loading -> Unit
                    }
                    is Result.Error -> _signupConfigurationUiState.value = SignupConfigurationUiState.Error(login.exception.message ?: "Unknown error")
                    is Result.Loading -> Unit
                }
                is Result.Error -> _signupConfigurationUiState.value = SignupConfigurationUiState.Error(saved.exception.message ?: "Unknown error")
                is Result.Loading -> Unit
            }
        }
    }

    fun consumeError() { _signupConfigurationUiState.value = SignupConfigurationUiState.Idle }

    fun deleteImageFromCache(context: Context, filename: String) {
        UploadUtility.deleteImageFromCache(context, filename)
    }
}

sealed class SignupConfigurationUiState {
    data object Idle : SignupConfigurationUiState()
    data object Loading : SignupConfigurationUiState()
    data object Success : SignupConfigurationUiState()
    data class Error(val message: String) : SignupConfigurationUiState()
}
