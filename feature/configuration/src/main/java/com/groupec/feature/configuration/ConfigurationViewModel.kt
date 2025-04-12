package com.groupec.feature.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.user.SaveUserDefaultUseCase
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(
    private val saveParameterUseCase: SaveParameterUseCase,
    private val saveUserDefaultUseCase: SaveUserDefaultUseCase
) : ViewModel() {

    private val _parameterUiState = MutableStateFlow<ParameterUiState>(ParameterUiState.Loading)
    val parameterUiState: StateFlow<ParameterUiState> = _parameterUiState

    init {
        getParameter()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getParameter() {
        viewModelScope.launch {

            var raisonSociale = ""

            val resultFlow = saveParameterUseCase().flatMapLatest { parameter ->
                when (parameter) {
                    is Result.Loading -> {
                        _parameterUiState.value = ParameterUiState.Loading
                        emptyFlow() // Pas de second flow à collecter pendant le chargement
                    }
                    is Result.Success -> {
                        // Lancer le second flow si succès
                        raisonSociale = parameter.data
                        saveUserDefaultUseCase()
                    }
                    is Result.Error -> {
                        _parameterUiState.value = ParameterUiState.Error(
                            parameter.exception.message ?: "Retrofit Unknown error"
                        )
                        emptyFlow() // Pas de second flow à collecter si le premier échoue
                    }
                }
            }

            // Collecter le second flow si le premier est collecté
            resultFlow.collect { user ->
                _parameterUiState.value = when (user) {
                    is Result.Success -> {
                        ParameterUiState.Success(raisonSociale)
                    }
                    is Result.Error -> ParameterUiState.Error(
                        user.exception.message ?: "Retrofit Unknown error"
                    )
                    else -> _parameterUiState.value // Ne pas mettre à jour l'état si c'est un Loading
                }
            }
        }
    }
}

sealed class ParameterUiState {
    data object Loading : ParameterUiState()
    data class Success(val raisonSociale: String) : ParameterUiState()
    data class Error(val message: String) : ParameterUiState()
}