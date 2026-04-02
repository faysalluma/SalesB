package com.groupec.feature.clientlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.ExportType
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.client.DeleteClientUseCase
import com.groupec.salesb.core.domain.client.GenerateClientListExcelUseCase
import com.groupec.salesb.core.domain.client.GenerateClientListPdfUseCase
import com.groupec.salesb.core.domain.client.GetAllClientsUseCase
import com.groupec.salesb.core.domain.client.GetPagedClientUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ClientListViewModel @Inject constructor(
    private val getPagedClientUseCase: GetPagedClientUseCase,
    private val deleteClientUseCase: DeleteClientUseCase,
    private val getAllClientsUseCase: GetAllClientsUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val generateClientListPdfUseCase: GenerateClientListPdfUseCase,
    private val generateClientListExcelUseCase: GenerateClientListExcelUseCase,
    private val getParameterUseCase: GetParameterUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _totalClientsCount = MutableStateFlow(0)
    val totalClientsCount: StateFlow<Int> = _totalClientsCount.asStateFlow()

    private val _deleteClientUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteClientUiState: StateFlow<FormUIState<*>> = _deleteClientUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()

    private val _userStoreState = MutableStateFlow(UserStore())
    val userStoreState: StateFlow<UserStore> = _userStoreState.asStateFlow()

    init {
        observeUserStore()
        observeTotalClientsCount()
    }

    private fun observeUserStore() {
        viewModelScope.launch {
            getUserStoreUseCase().collectLatest { userStore ->
                _userStoreState.value = userStore
            }
        }
    }

    val pagedClients: Flow<PagingData<Client>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true
            getPagedClientUseCase(query)
                .onCompletion {
                    _isSearching.value = false
                }
        }
        .cachedIn(viewModelScope)

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun observeTotalClientsCount() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                _totalClientsCount.value = when (val result = getAllClientsUseCase(query)) {
                    is Result.Success -> result.data.size
                    else -> 0
                }
            }
        }
    }

    fun deleteClient(id: Int) {
        _deleteClientUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteClientUseCase(id)) {
                is Result.Success -> {
                    _totalClientsCount.value = (_totalClientsCount.value - 1).coerceAtLeast(0)
                    _deleteClientUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteClientUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting client")
                }

                else -> {}
            }
        }
    }

    fun exportClientsToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllClientsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val clients = result.data
                    if (clients.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_clients_to_export)
                        )
                        return@launch
                    }
                    try {
                        val logoUrl = getParameterUseCase().first().logo
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateClientListPdfUseCase(
                                activityContext,
                                clients,
                                _searchQuery.value,
                                logoUrl
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "clients-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
                            )
                        }
                        _exportPdfUiState.value = FormUIState.Success(file)
                    } catch (exception: Exception) {
                        _exportPdfUiState.value = FormUIState.Error(
                            exception.message ?: activityContext.getString(R.string.export_pdf_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportPdfUiState.value = FormUIState.Error(
                        result.exception.message ?: activityContext.getString(R.string.export_pdf_failed)
                    )
                }

                else -> {}
            }
        }
    }

    fun exportClientsToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            when (val result = getAllClientsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val clients = result.data
                    if (clients.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_clients_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateClientListExcelUseCase(activityContext, clients, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "clients-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
                            )
                        }
                        _exportExcelUiState.value = FormUIState.Success(file)
                    } catch (exception: Exception) {
                        _exportExcelUiState.value = FormUIState.Error(
                            exception.message ?: activityContext.getString(R.string.export_excel_failed)
                        )
                    }
                }

                is Result.Error -> {
                    _exportExcelUiState.value = FormUIState.Error(
                        result.exception.message ?: activityContext.getString(R.string.export_excel_failed)
                    )
                }

                else -> {}
            }
        }
    }

    fun resetExportState() {
        _exportPdfUiState.value = FormUIState.Idle
    }

    fun resetExportExcelState() {
        _exportExcelUiState.value = FormUIState.Idle
    }

    fun resetFlow() {
        _deleteClientUiState.value = FormUIState.Idle
    }
}
