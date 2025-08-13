package com.groupec.feature.salelist

import android.content.Context
import android.print.PrintManager
import androidx.compose.foundation.layout.ColumnScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.BitmapPrintAdapter
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.currentDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.sale.GenerateInvoicePdfUseCase
import com.groupec.salesb.core.domain.sale.GetSaleUseCase
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.print.Print
import com.groupec.salesb.core.sendEmailWithAttachment
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import java.io.File

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSaleUseCase:GetSaleUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val generateInvoicePdfUseCase: GenerateInvoicePdfUseCase
) : ViewModel() {

    private val defaultDate = currentDateString(pattern = "yyyy-MM-dd")
    private val _startDate = MutableStateFlow(defaultDate)
    private val _endDate = MutableStateFlow(defaultDate)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _parameter = MutableStateFlow(Parameter())
    val parameter : StateFlow<Parameter> = _parameter.asStateFlow()

    // Bluetooth
    val bluetoothPrint = Print(context)
    private val _printUiState = MutableSharedFlow<FormUIState<Unit>>(replay = 0)
    val printUiState: SharedFlow<FormUIState<Unit>> = _printUiState.asSharedFlow()

    init {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().first()
        }
    }

    val pagedProducts: Flow<PagingData<Sale>> = combine(_searchQuery, _startDate, _endDate) { query, start, end ->
            mapOf(
                "totalprix" to query,
                "startDate" to start,
                "endDate" to end
            )
        }
        .flatMapLatest { searchParams ->
            getSaleUseCase(searchParams)
                .onStart { _isSearching.value = true /* Indique qu'une recherche commence */  }
                .onCompletion {
                    // Nb: Au first load, on ne rentre jamais dans le onCompletion à cause de la paignationData qui est infini,
                    // ici on rentre dedans à cause du cancel du flatMapLastest
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun updateStartDateQuery(newQuery: String) {
        _startDate.value = newQuery
    }

    fun updateEndDateQuery(newQuery: String) {
        _endDate.value = newQuery
    }

    fun printThermalReceipt(sale: Sale, parameter: Parameter) {
        viewModelScope.launch(Dispatchers.IO) {
            _printUiState.emit(FormUIState.Loading)

            val result = bluetoothPrint.printWithResult(
                getDrawableResIdIfExists(context),
                sale = sale,
                parameter = parameter
            )
            if (result.isSuccess) {
                _printUiState.emit(FormUIState.Success(Unit))
            } else {
                _printUiState.emit(FormUIState.Error(result.exceptionOrNull()?.message ?: "Unknown error"))
            }
        }
    }

    fun onPrint(activityContext: Context, sale: Sale, parameter: Parameter, invoicing: Invoicing) {
        viewModelScope.launch {
            val pdfBytes = generateInvoicePdfUseCase(activityContext, sale, parameter, invoicing)
            val printAdapter = BitmapPrintAdapter(pdfBytes)

            val printManager = activityContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print("MonPDF", printAdapter, null)
        }
    }

    fun sendByEmail(activityContext: Context, sale: Sale, parameter: Parameter, invoicing: Invoicing) {
        viewModelScope.launch {
            val pdfBytes = generateInvoicePdfUseCase(activityContext, sale, parameter, invoicing)
            val file = File(activityContext.cacheDir, "invoice.pdf")
            file.outputStream().use { it.write(pdfBytes) }

            // 3. Envoyer l'email avec pièce jointe
            activityContext.sendEmailWithAttachment(
                addresses = arrayOf(invoicing.email),
                subject = activityContext.getString(R.string.your_invoice_object, parameter.raisonsociale),
                body = activityContext.getString(R.string.your_invoice_body),
                attachment = file
            )
        }
    }

}
