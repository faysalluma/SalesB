package com.groupec.salesb.ui


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.CheckSubscriptionExpirationUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.domain.user.LogoutPasswordUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val logoutPasswordUseCase: LogoutPasswordUseCase,
    private val checkSubscriptionExpirationUseCase: CheckSubscriptionExpirationUseCase
) : ViewModel() {

    private val _userStore = MutableStateFlow(UserStore())
    val userStore: StateFlow<UserStore> = _userStore.asStateFlow()

    private val _parameter = MutableStateFlow(Parameter())
    val parameter: StateFlow<Parameter> = _parameter.asStateFlow()

    private val _logoutUiState = MutableStateFlow<UIState<*>>(UIState.Loading)
    val logoutUiState: StateFlow<UIState<*>> = _logoutUiState.asStateFlow()

    init {
        observeParameterStore()
        getUserStore()
    }

    private fun observeParameterStore() {
        viewModelScope.launch {
            getParameterUseCase()
                .distinctUntilChanged()
                .collectLatest { parameter ->
                    _parameter.value = parameter
                }
        }
    }

    fun getParameterStore() {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().distinctUntilChanged().first()
        }
    }

    fun getUserStore() {
        viewModelScope.launch {
            _userStore.value =
                getUserStoreUseCase().distinctUntilChanged().firstOrNull() ?: UserStore()
        }
    }

    fun logout() {
        viewModelScope.launch {
            when (val result = logoutPasswordUseCase()) {
                is Result.Success -> _logoutUiState.value = UIState.Success(Unit)
                is Result.Error -> _logoutUiState.value =  UIState.Error(result.exception.localizedMessage ?: "Error when logout")
                else -> {}
            }
        }
    }

    fun checkSubscriptionExpiration() {
        viewModelScope.launch {
            val isExpired = checkSubscriptionExpirationUseCase()
            if (isExpired == true) {
                logout()
            }
        }
    }

    fun resetFlow() {
        _logoutUiState.value = UIState.Loading
    }
}
