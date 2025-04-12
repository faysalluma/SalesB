package com.groupec.feature.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.user.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _forgotPasswordUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val forgotPasswordUiState: StateFlow<FormUIState<*>> = _forgotPasswordUiState.asStateFlow()

    fun resetPassword(email: String) {
        _forgotPasswordUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = forgotPasswordUseCase(email)) {
                is Result.Success -> {
                    _forgotPasswordUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _forgotPasswordUiState.value =
                        FormUIState.Error(result.exception.localizedMessage ?: "Error when reset password")
                }

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _forgotPasswordUiState.value = FormUIState.Idle
    }

}
