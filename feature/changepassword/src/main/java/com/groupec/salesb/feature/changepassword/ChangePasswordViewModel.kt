package com.groupec.salesb.feature.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.user.ChangePasswordUseCase
import com.groupec.salesb.core.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.groupec.salesb.core.Result
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _changePasswordUiState =
        MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Idle)
    val changePasswordUiState: StateFlow<ChangePasswordUiState> =
        _changePasswordUiState.asStateFlow()

    fun changePassword(userId: Int, ancPassword: String = "", password: String) {
        _changePasswordUiState.value = ChangePasswordUiState.Loading
        viewModelScope.launch {
            when (val result = changePasswordUseCase(userId, ancPassword, password)) {
                is Result.Success -> _changePasswordUiState.value =
                    ChangePasswordUiState.Success(result.data)

                is Result.Error -> _changePasswordUiState.value =
                    ChangePasswordUiState.Error(
                        result.exception.message ?: "Failed to change user password"
                    )

                else -> {}
            }
        }
    }

    fun resetFlow() {
        _changePasswordUiState.value = ChangePasswordUiState.Idle
    }
}

sealed class ChangePasswordUiState {
    data object Idle : ChangePasswordUiState()
    data object Loading : ChangePasswordUiState()
    data class Success(val user: User) : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}
