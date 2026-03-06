package com.groupec.feature.signup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import com.groupec.salesb.core.domain.signup.SaveSignupConfigurationUseCase
import com.groupec.salesb.core.domain.user.SaveUserDefaultUseCase
import com.groupec.salesb.core.model.data.SignupConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val saveSignupConfigurationUseCase: SaveSignupConfigurationUseCase,
    private val saveParameterUseCase: SaveParameterUseCase,
    private val saveUserDefaultUseCase: SaveUserDefaultUseCase
) : ViewModel() {

    private val _signupConfigurationUiState = MutableStateFlow<SignupConfigurationUiState>(SignupConfigurationUiState.Idle)
    val signupConfigurationUiState: StateFlow<SignupConfigurationUiState> = _signupConfigurationUiState

    @OptIn(ExperimentalCoroutinesApi::class)
    fun saveInitialConfiguration(
        configuration: SignupConfiguration,
        uriLogo: Uri?
    ) {
        viewModelScope.launch {
            _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
            when (val saveSignupResult = saveSignupConfigurationUseCase(configuration, uriLogo)) {
                is Result.Error -> {
                    _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                        saveSignupResult.exception.message ?: "Unknown error"
                    )
                    return@launch
                }
                else -> {}
            }

            val resultFlow = saveParameterUseCase().flatMapLatest { parameter ->
                when (parameter) {
                    is Result.Loading -> {
                        _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
                        emptyFlow()
                    }

                    is Result.Success -> {
                        saveUserDefaultUseCase()
                    }

                    is Result.Error -> {
                        _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                            parameter.exception.message ?: "Unknown error"
                        )
                        emptyFlow()
                    }
                }
            }

            resultFlow.collect { user ->
                _signupConfigurationUiState.value = when (user) {
                    is Result.Loading -> SignupConfigurationUiState.Loading
                    is Result.Success -> SignupConfigurationUiState.Success
                    is Result.Error -> SignupConfigurationUiState.Error(
                        user.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun consumeError() {
        _signupConfigurationUiState.value = SignupConfigurationUiState.Idle
    }

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
