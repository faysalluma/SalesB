package com.groupec.feature.salelist

import android.content.Context
import android.print.PrintManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.BitmapPrintAdapter
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.sale.GenerateSaleListExcelUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.sale.GenerateSaleListPdfUseCase
import com.groupec.salesb.core.domain.sale.GenerateInvoicePdfUseCase
import com.groupec.salesb.core.domain.sale.GetAllSalesUseCase
import com.groupec.salesb.core.domain.sale.GetSaleUseCase
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.print.Print
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import com.groupec.salesb.core.sendEmailWithAttachment
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleListViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getSaleUseCase:GetSaleUseCase,
    private val getAllSalesUseCase: GetAllSalesUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val generateInvoicePdfUseCase: GenerateInvoicePdfUseCase,
    private val generateSaleListPdfUseCase: GenerateSaleListPdfUseCase,
    private val generateSaleListExcelUseCase: GenerateSaleListExcelUseCase
) : ViewModel() {

    private val defaultDate = currentDateString(pattern = "yyyy-MM-dd")
    private val _startDate = MutableStateFlow(defaultDate)
    private val _endDate = MutableStateFlow(defaultDate)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val saleSearchParams: Flow<Map<String, String>> =
        combine(_searchQuery, _startDate, _endDate) { query, start, end ->
            buildSaleSearchParams(query, start, end)
        }

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _parameter = MutableStateFlow(Parameter())
    val parameter: StateFlow<Parameter> = _parameter.asStateFlow()

    private val _totalSalesCount = MutableStateFlow(0)
    val totalSalesCount: StateFlow<Int> = _totalSalesCount.asStateFlow()

    private val _totalSalesAmount = MutableStateFlow(0.0)
    val totalSalesAmount: StateFlow<Double> = _totalSalesAmount.asStateFlow()

    // Bluetooth
    val bluetoothPrint = Print(context)
    private val _printUiState = MutableSharedFlow<FormUIState<Unit>>(replay = 0)
    val printUiState: SharedFlow<FormUIState<Unit>> = _printUiState.asSharedFlow()

    // Save pdf file
    val fileName = "receipt-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
    private val _saveReceiptToDownloads = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val saveReceiptToDownloads: StateFlow<FormUIState<File>> = _saveReceiptToDownloads.asStateFlow()

    private val _invoicePrintUiState = MutableStateFlow<FormUIState<Unit>>(FormUIState.Idle)
    val invoicePrintUiState: StateFlow<FormUIState<Unit>> = _invoicePrintUiState.asStateFlow()

    private val _sendInvoiceEmailUiState = MutableStateFlow<FormUIState<Unit>>(FormUIState.Idle)
    val sendInvoiceEmailUiState: StateFlow<FormUIState<Unit>> = _sendInvoiceEmailUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()

    init {
        observeSalesSummary()
    }

    fun getParameter() {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().first()
        }
    }

    val pagedProducts: Flow<PagingData<Sale>> =
        saleSearchParams
            .flatMapLatest { searchParams ->
                getSaleUseCase(searchParams)
                    .onStart { _isSearching.value = true /* Indique qu'une recherche commence */ }
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

    fun exportSalesToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            val searchParams = buildSaleSearchParams()
            when (val result = getAllSalesUseCase(searchParams)) {
                is Result.Success -> {
                    val sales = result.data
                    if (sales.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_sales_to_export)
                        )
                        return@launch
                    }
                    try {
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateSaleListPdfUseCase(
                                activityContext,
                                sales,
                                _searchQuery.value,
                                _startDate.value,
                                _endDate.value,
                                _parameter.value.devise,
                                _parameter.value.serviceview
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "sales-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
                            )
                        }
                        _exportPdfUiState.value = FormUIState.Success(file)
                    } catch (e: Exception) {
                        _exportPdfUiState.value = FormUIState.Error(
                            e.message ?: activityContext.getString(R.string.export_pdf_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportPdfUiState.value = FormUIState.Error(
                        result.exception.message
                            ?: activityContext.getString(R.string.export_pdf_failed)
                    )
                }

                else -> {}
            }
        }
    }

    fun exportSalesToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            val searchParams = buildSaleSearchParams()
            when (val result = getAllSalesUseCase(searchParams)) {
                is Result.Success -> {
                    val sales = result.data
                    if (sales.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_sales_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateSaleListExcelUseCase(
                                activityContext,
                                sales,
                                _searchQuery.value,
                                _startDate.value,
                                _endDate.value,
                                _parameter.value.devise,
                                _parameter.value.serviceview
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "sales-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
                            )
                        }
                        _exportExcelUiState.value = FormUIState.Success(file)
                    } catch (e: Exception) {
                        _exportExcelUiState.value = FormUIState.Error(
                            e.message ?: activityContext.getString(R.string.export_excel_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportExcelUiState.value = FormUIState.Error(
                        result.exception.message
                            ?: activityContext.getString(R.string.export_excel_failed)
                    )
                }

                else -> {}
            }
        }
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
                _printUiState.emit(
                    FormUIState.Error(
                        result.exceptionOrNull()?.message ?: "Unknown error"
                    )
                )
            }
        }
    }

    fun onPrint(activityContext: Context, sale: Sale, parameter: Parameter, invoicing: Invoicing) {
        viewModelScope.launch {
            _invoicePrintUiState.value = FormUIState.Loading
            runCatching {
                val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)

                val printAdapter = BitmapPrintAdapter(pdfBytes)
                val printManager =
                    activityContext.getSystemService(Context.PRINT_SERVICE) as PrintManager
                printManager.print("MyPdfJob", printAdapter, null)
                _invoicePrintUiState.value = FormUIState.Success(Unit)
            }.onFailure { exception ->
                _invoicePrintUiState.value = FormUIState.Error(
                    exception.localizedMessage ?: "Error when opening the system print dialog"
                )
            }
        }
    }

    fun sendByEmail(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ) {
        viewModelScope.launch {
            _sendInvoiceEmailUiState.value = FormUIState.Loading
            runCatching {
                val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)
                val file = withContext(Dispatchers.IO) {
                    File(activityContext.cacheDir, fileName).apply {
                        outputStream().use { it.write(pdfBytes) }
                    }
                }

                activityContext.sendEmailWithAttachment(
                    addresses = arrayOf(invoicing.email),
                    subject = activityContext.getString(
                        R.string.your_invoice_object,
                        parameter.raisonsociale
                    ),
                    body = activityContext.getString(R.string.your_invoice_body),
                    attachment = file
                )
                _sendInvoiceEmailUiState.value = FormUIState.Success(Unit)
            }.onFailure { exception ->
                _sendInvoiceEmailUiState.value = FormUIState.Error(
                    exception.localizedMessage ?: "Error when preparing the invoice email"
                )
            }
        }
    }

    fun savePdfToDownloads(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ) {
        viewModelScope.launch {
            _saveReceiptToDownloads.value = FormUIState.Loading
            try {
                val pdfBytes = generatePdf(activityContext, sale, parameter, invoicing)
                val file = withContext(Dispatchers.IO) {
                    activityContext.savePdfToDownloads(pdfBytes, fileName)
                }
                _saveReceiptToDownloads.value = FormUIState.Success(file)
            } catch (e: Exception) {
                _saveReceiptToDownloads.value = FormUIState.Error(
                    e.localizedMessage ?: "Error when saving receipt file to downloads folder"
                )
            }
        }
    }

    private suspend fun generatePdf(
        activityContext: Context,
        sale: Sale,
        parameter: Parameter,
        invoicing: Invoicing
    ): ByteArray {
        return withContext(Dispatchers.Default) {
            generateInvoicePdfUseCase(activityContext, sale, parameter, invoicing)
        }
    }

    fun resetExportState() {
        _exportPdfUiState.value = FormUIState.Idle
    }

    fun resetExportExcelState() {
        _exportExcelUiState.value = FormUIState.Idle
    }

    fun resetInvoiceActionState() {
        _saveReceiptToDownloads.value = FormUIState.Idle
        _invoicePrintUiState.value = FormUIState.Idle
        _sendInvoiceEmailUiState.value = FormUIState.Idle
    }

    private fun observeSalesSummary() {
        viewModelScope.launch {
            saleSearchParams.collectLatest { searchParams ->
                when (val result = getAllSalesUseCase(searchParams)) {
                    is Result.Success -> {
                        _totalSalesCount.value = result.data.size
                        _totalSalesAmount.value = result.data.sumOf { it.totalprix }
                    }

                    else -> {
                        _totalSalesCount.value = 0
                        _totalSalesAmount.value = 0.0
                    }
                }
            }
        }
    }

    private fun buildSaleSearchParams(
        query: String = _searchQuery.value,
        startDate: String = _startDate.value,
        endDate: String = _endDate.value
    ): Map<String, String> {
        return mapOf(
            "totalprix" to query,
            "startDate" to startDate,
            "endDate" to endDate
        )
    }
}
