package com.groupec.feature.handleservice

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.handleservice.UpdateCashEnabledUseCase
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.handleservice.GetHandleServiceParametersUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActiveClientUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePaymentModeUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePrinterUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateShowImageOnProductUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateUseIntForPriceAndAmountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HandleServiceUiState(
    val isServiceBusiness: Boolean = false,
    val showImageOnProduct: Boolean = false,
    val useIntForPriceAndAmount: Boolean = false,
    val activePaymentMode: Boolean = false,
    val activeClient: Boolean = true,
    val activePrinter: Boolean = false,
    val cashEnabled: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class HandleServiceViewModel @Inject constructor(
    private val getHandleServiceParametersUseCase: GetHandleServiceParametersUseCase,
    private val updateShowImageOnProductUseCase: UpdateShowImageOnProductUseCase,
    private val updateUseIntForPriceAndAmountUseCase: UpdateUseIntForPriceAndAmountUseCase,
    private val updateActivePaymentModeUseCase: UpdateActivePaymentModeUseCase,
    private val updateActiveClientUseCase: UpdateActiveClientUseCase,
    private val updateActivePrinterUseCase: UpdateActivePrinterUseCase,
    private val updateCashEnabledUseCase: UpdateCashEnabledUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HandleServiceUiState())
    val uiState: StateFlow<HandleServiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getHandleServiceParametersUseCase().collectLatest { parameter ->
                _uiState.update { it.copy(
                    isServiceBusiness = parameter.isServiceBusiness,
                    showImageOnProduct = parameter.showimageonproduct,
                    useIntForPriceAndAmount = parameter.useintforpriceandamout,
                    activePaymentMode = parameter.activepaymentmode,
                    activeClient = parameter.activeClient,
                    activePrinter = parameter.activeprinter,
                    cashEnabled = parameter.cashEnabled,
                ) }
            }
        }
    }

    fun updateShowImageOnProduct(value: Boolean) = save { updateShowImageOnProductUseCase(value) }
    fun updateUseIntForPriceAndAmount(value: Boolean) = save { updateUseIntForPriceAndAmountUseCase(value) }
    fun updateActivePaymentMode(value: Boolean) = save { updateActivePaymentModeUseCase(value) }
    fun updateActiveClient(value: Boolean) = save { updateActiveClientUseCase(value) }
    fun updateActivePrinter(value: Boolean) = save { updateActivePrinterUseCase(value) }
    fun updateCashEnabled(value: Boolean) = save { updateCashEnabledUseCase(value) }

    private fun save(operation: suspend () -> Result<Unit>) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                when (val result = operation()) {
                    is Result.Error -> _uiState.update { it.copy(errorMessage = result.exception.message) }
                    else -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
