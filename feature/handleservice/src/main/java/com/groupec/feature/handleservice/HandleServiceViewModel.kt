package com.groupec.feature.handleservice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.handleservice.GetHandleServiceParametersUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePaymentModeUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePrinterUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateServiceViewUseCase
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
    val serviceView: Boolean = false,
    val showImageOnProduct: Boolean = false,
    val useIntForPriceAndAmount: Boolean = false,
    val activePaymentMode: Boolean = false,
    val activePrinter: Boolean = false,
    val isUseIntForPriceAndAmountDisabled: Boolean = false
)

@HiltViewModel
class HandleServiceViewModel @Inject constructor(
    private val getHandleServiceParametersUseCase: GetHandleServiceParametersUseCase,
    private val updateServiceViewUseCase: UpdateServiceViewUseCase,
    private val updateShowImageOnProductUseCase: UpdateShowImageOnProductUseCase,
    private val updateUseIntForPriceAndAmountUseCase: UpdateUseIntForPriceAndAmountUseCase,
    private val updateActivePaymentModeUseCase: UpdateActivePaymentModeUseCase,
    private val updateActivePrinterUseCase: UpdateActivePrinterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HandleServiceUiState())
    val uiState: StateFlow<HandleServiceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getHandleServiceParametersUseCase().collectLatest { parameter ->
                _uiState.value = HandleServiceUiState(
                    serviceView = parameter.serviceview,
                    showImageOnProduct = parameter.showimageonproduct,
                    useIntForPriceAndAmount = parameter.useintforpriceandamout,
                    activePaymentMode = parameter.activepaymentmode,
                    activePrinter = parameter.activeprinter,
                    isUseIntForPriceAndAmountDisabled = parameter.serviceview
                )
            }
        }
    }

    fun updateServiceView(value: Boolean) {
        _uiState.update { current ->
            if (value) {
                current.copy(
                    serviceView = true,
                    useIntForPriceAndAmount = true,
                    isUseIntForPriceAndAmountDisabled = true
                )
            } else {
                current.copy(
                    serviceView = false,
                    isUseIntForPriceAndAmountDisabled = false
                )
            }
        }

        viewModelScope.launch {
            updateServiceViewUseCase(value)
        }
    }

    fun updateShowImageOnProduct(value: Boolean) {
        _uiState.update { it.copy(showImageOnProduct = value) }

        viewModelScope.launch {
            updateShowImageOnProductUseCase(value)
        }
    }

    fun updateUseIntForPriceAndAmount(value: Boolean) {
        if (_uiState.value.isUseIntForPriceAndAmountDisabled) return

        _uiState.update { it.copy(useIntForPriceAndAmount = value) }

        viewModelScope.launch {
            updateUseIntForPriceAndAmountUseCase(value)
        }
    }

    fun updateActivePaymentMode(value: Boolean) {
        _uiState.update { it.copy(activePaymentMode = value) }

        viewModelScope.launch {
            updateActivePaymentModeUseCase(value)
        }
    }

    fun updateActivePrinter(value: Boolean) {
        _uiState.update { it.copy(activePrinter = value) }

        viewModelScope.launch {
            updateActivePrinterUseCase(value)
        }
    }
}
