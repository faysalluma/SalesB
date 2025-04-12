package com.groupec.feature.login


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.user.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.Credentials
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _loginUiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginUiState: StateFlow<LoginUiState> = _loginUiState.asStateFlow()

    fun login(credentials: Credentials) {
        _loginUiState.value = LoginUiState.Loading
        viewModelScope.launch {
            when (val result = loginUseCase(credentials.email, credentials.password)) {
                is Result.Success -> {
                    _loginUiState.value = LoginUiState.Success(result.data)
                }
                is Result.Error -> _loginUiState.value =
                    LoginUiState.Error(result.exception.message ?: "Failed to check user")
                else -> {}
            }
        }
    }

    fun resetFlow() {
        _loginUiState.value = LoginUiState.Idle
    }
}

sealed class LoginUiState {
    data object Idle : LoginUiState()
    data object Loading : LoginUiState()
    data class Success(val userInfo: Pair<User, Boolean>) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}
