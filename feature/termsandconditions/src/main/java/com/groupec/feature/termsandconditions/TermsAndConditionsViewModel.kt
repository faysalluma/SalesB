package com.groupec.feature.termsandconditions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.AcceptTermsAndConditionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TermsAndConditionsViewModel @Inject constructor(
    private val acceptTermsAndConditionsUseCase: AcceptTermsAndConditionsUseCase
): ViewModel() {

    private var _termsAndConditionsUiState = MutableStateFlow<TermsAndConditionsUiState>(TermsAndConditionsUiState.Idle)
    val termsAndConditionsUiState: StateFlow<TermsAndConditionsUiState> = _termsAndConditionsUiState.asStateFlow()

    fun acceptTermsAndConditions() {
        _termsAndConditionsUiState.value = TermsAndConditionsUiState.Loading
        viewModelScope.launch {
            when (val result = acceptTermsAndConditionsUseCase()) {
                is Result.Success ->  _termsAndConditionsUiState.value = TermsAndConditionsUiState.Success
                is Result.Error -> _termsAndConditionsUiState.value = TermsAndConditionsUiState.Error(
                    result.exception.localizedMessage ?: "Failed when saving accept terms and conditions "
                )
                else -> {}
            }
        }
    }

    fun resetFlow() {
        _termsAndConditionsUiState.value = TermsAndConditionsUiState.Idle
    }
}

sealed class TermsAndConditionsUiState {
    data object Idle: TermsAndConditionsUiState()
    data object Loading: TermsAndConditionsUiState()
    data object Success: TermsAndConditionsUiState()
    data class Error(val message: String): TermsAndConditionsUiState()
}