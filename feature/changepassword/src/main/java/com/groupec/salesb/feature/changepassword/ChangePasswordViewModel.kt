package com.groupec.salesb.feature.changepassword


import androidx.lifecycle.ViewModel
import com.groupec.salesb.core.domain.user.LoginUseCase
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.Password
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _changePasswordUiState =
        MutableStateFlow<ChangePasswordUiState>(ChangePasswordUiState.Loading)
    val changePasswordUiState: StateFlow<ChangePasswordUiState> = _changePasswordUiState

    fun changePassword(passwords: Password) {
        /*viewModelScope.launch {
            when (val result = loginUseCase(credentials.email, credentials.password)) {
                is Result.Loading -> ChangePasswordUiState.Loading
                is Result.Success -> _loginUiState.value = ChangePasswordUiState.Success(result.data)
                is Result.Error -> _loginUiState.value =
                    ChangePasswordUiState.Error(result.exception.message ?: "Failed to change user password")
            }
        }*/
    }
}

sealed class ChangePasswordUiState {
    data object Loading : ChangePasswordUiState()
    data class Success(val user: User) : ChangePasswordUiState()
    data class Error(val message: String) : ChangePasswordUiState()
}
