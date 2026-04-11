package com.groupec.feature.signup

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UploadUtility
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import com.groupec.salesb.core.domain.signup.SaveSignupConfigurationUseCase
import com.groupec.salesb.core.domain.user.CheckUserEmailExistsUseCase
import com.groupec.salesb.core.domain.user.SaveUserStoreByIdUseCase
import com.groupec.salesb.core.model.data.SignupConfiguration
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val saveSignupConfigurationUseCase: SaveSignupConfigurationUseCase,
    private val saveParameterUseCase: SaveParameterUseCase,
    private val checkUserEmailExistsUseCase: CheckUserEmailExistsUseCase,
    private val saveUserStoreByIdUseCase: SaveUserStoreByIdUseCase
) : ViewModel() {

    private val _signupConfigurationUiState = MutableStateFlow<SignupConfigurationUiState>(SignupConfigurationUiState.Idle)
    val signupConfigurationUiState: StateFlow<SignupConfigurationUiState> = _signupConfigurationUiState

    fun saveInitialConfiguration(
        configuration: SignupConfiguration,
        uriLogo: Uri?
    ) {
        viewModelScope.launch {
            _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
            when (val saveSignupResult = saveSignupConfigurationUseCase(configuration, uriLogo)) {
                is Result.Success -> {
                    when (val saveParameterResult = saveParameterUseCase(saveSignupResult.data)) {
                        is Result.Success -> {
                            val userId = saveSignupResult.data.userid
                            if (userId == null) {
                                _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                                    "User id not found in signup response"
                                )
                            } else {
                                when (val saveUserResult = saveUserStoreByIdUseCase(userId)) {
                                    is Result.Success -> {
                                        _signupConfigurationUiState.value = SignupConfigurationUiState.Success
                                    }
                                    is Result.Error -> {
                                        _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                                            saveUserResult.exception.message ?: "Unknown error"
                                        )
                                    }
                                    is Result.Loading -> {
                                        _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
                                    }
                                }
                            }
                        }
                        is Result.Error -> {
                            _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                                saveParameterResult.exception.message ?: "Unknown error"
                            )
                        }
                        is Result.Loading -> {
                            _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
                        }
                    }
                }
                is Result.Error -> {
                    _signupConfigurationUiState.value = SignupConfigurationUiState.Error(
                        saveSignupResult.exception.message ?: "Unknown error"
                    )
                }
                is Result.Loading -> {
                    _signupConfigurationUiState.value = SignupConfigurationUiState.Loading
                }
            }
        }
    }

    fun consumeError() {
        _signupConfigurationUiState.value = SignupConfigurationUiState.Idle
    }

    suspend fun checkEmailExists(email: String): Result<Boolean> {
        return checkUserEmailExistsUseCase(email)
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
