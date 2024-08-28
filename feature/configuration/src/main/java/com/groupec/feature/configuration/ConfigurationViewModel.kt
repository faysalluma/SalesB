package com.groupec.feature.configuration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.SaveParameterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigurationViewModel @Inject constructor(private val saveParameterUseCase: SaveParameterUseCase) : ViewModel() {

    private val _parameterUiState = MutableStateFlow<ParameterUiState>(ParameterUiState.Loading)
    val parameterUiState: StateFlow<ParameterUiState> = _parameterUiState

    init {
        getParameter()
    }
    private fun getParameter() {
        viewModelScope.launch {
            saveParameterUseCase()
                .collect { result ->
                    _parameterUiState.value = when (result) {
                        is Result.Loading-> ParameterUiState.Loading
                        is Result.Success -> {
                            ParameterUiState.Success(result.data)
                        }
                        is Result.Error -> ParameterUiState.Error(
                            result.exception.message ?: "Retrofit Unknown error"
                        )
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