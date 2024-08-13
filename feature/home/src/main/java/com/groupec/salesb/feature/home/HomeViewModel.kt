package com.groupec.salesb.feature.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.GetParameterUseCase
import com.groupec.salesb.core.domain.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getParameterUseCase: GetParameterUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase
) : ViewModel() {

    private val _configUiState = MutableStateFlow<ConfigUiState>(ConfigUiState.Loading)
    val configUiState: StateFlow<ConfigUiState> = _configUiState

    init {
        viewModelScope.launch {
                getParameterUseCase().flatMapLatest { parameter ->
                    if (parameter.raisonsociale.isEmpty()) {
                        // Mettre à jour _userUiState
                        _configUiState.value = ConfigUiState.Configuration
                    } else {
                        // Récupérer userStore avant de continuer
                        val userStore = getUserStoreUseCase().firstOrNull() ?: UserStore()
                        // Mettre à jour _userUiState
                        _configUiState.value = ConfigUiState.Success(userStore)
                    }
                    _configUiState
                }.collect { userState ->
                    if (userState is ConfigUiState.Success) {
                        if (userState.userStore.id.isEmpty()) {
                            _configUiState.value = ConfigUiState.Login
                        }
                    }
                }
        }
    }
}
sealed class ConfigUiState {
    data object Loading : ConfigUiState()
    data object Configuration : ConfigUiState()
    data object Login : ConfigUiState()
    data class Success(val userStore: UserStore) : ConfigUiState()
}
