package com.groupec.salesb.feature.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.GetParameterUseCase
import com.groupec.salesb.core.domain.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getParameterUseCase: GetParameterUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase
) : ViewModel() {

    private val _parameterUiState = MutableStateFlow<ParameterUiState>(ParameterUiState.Loading)
    val parameterUiState: StateFlow<ParameterUiState> = _parameterUiState

    private val _userUiState = MutableStateFlow<UserUiState>(UserUiState.Loading)
    val userUiState: StateFlow<UserUiState> = _userUiState

    init {
        getParameters()
        getUserStore()
    }
    fun getParameters() {
        viewModelScope.launch {
           getParameterUseCase()
                .collect { result ->
                    _parameterUiState.value = ParameterUiState.Success(result)
                }
        }
    }

    fun getUserStore() {
        viewModelScope.launch {
            getUserStoreUseCase()
                .collect { result ->
                    _userUiState.value = UserUiState.Success(result)
                }
        }
    }
}
sealed class ParameterUiState {
    data object Loading : ParameterUiState()
    data class Success(val paremeter: Parameter) : ParameterUiState()
}

sealed class UserUiState {
    data object Loading : UserUiState()
    data class Success(val userStore: UserStore) : UserUiState()
}
