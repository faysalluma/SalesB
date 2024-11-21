package com.groupec.salesb.feature.loading


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoadingViewModel @Inject constructor(
    private val getParameterUseCase: GetParameterUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase
) : ViewModel() {

    private val _configUiState = MutableStateFlow<ConfigUiState>(ConfigUiState.Loading)
    val configUiState: StateFlow<ConfigUiState> = _configUiState.asStateFlow()

    fun loadScreen() {
        viewModelScope.launch {
            val parameter = getParameterUseCase().firstOrNull() ?: Parameter()
            if (parameter.raisonsociale.isEmpty()) {
                // Mettre à jour _userUiState
                _configUiState.value = ConfigUiState.Configuration
            } else {
                // Récupérer userStore avant de continuer
                val userStore = getUserStoreUseCase().firstOrNull() ?: UserStore()
                if (userStore.id.isEmpty()) {
                    _configUiState.value = ConfigUiState.Login(parameter.raisonsociale)
                } else {
                    _configUiState.value = ConfigUiState.Home
                }
            }
        }
    }
}

sealed class ConfigUiState {
    data object Loading : ConfigUiState()
    data object Configuration : ConfigUiState()
    data class Login(val raisonSociale: String) : ConfigUiState()
    data object Home : ConfigUiState()
}
