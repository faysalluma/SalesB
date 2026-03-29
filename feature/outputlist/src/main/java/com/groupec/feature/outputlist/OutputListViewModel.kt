package com.groupec.feature.outputlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.category.DeleteOutputUseCase
import com.groupec.salesb.core.domain.output.GenerateOutputListExcelUseCase
import com.groupec.salesb.core.domain.output.GenerateOutputListPdfUseCase
import com.groupec.salesb.core.domain.output.GetAllOutputsUseCase
import com.groupec.salesb.core.domain.output.GetOutputUseCase
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class OutputListViewModel @Inject constructor(
    private val getOutputUseCase: GetOutputUseCase,
    private val deleteOutputUseCase: DeleteOutputUseCase,
    private val getAllOutputsUseCase: GetAllOutputsUseCase,
    private val generateOutputListPdfUseCase: GenerateOutputListPdfUseCase,
    private val generateOutputListExcelUseCase: GenerateOutputListExcelUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _totalOutputsCount = MutableStateFlow(0)
    val totalOutputsCount: StateFlow<Int> = _totalOutputsCount.asStateFlow()

    private val _deleteOutputUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteOutputUiState: StateFlow<FormUIState<*>> = _deleteOutputUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()

    init {
        observeTotalOutputsCount()
    }

    val pagedOutputs: Flow<PagingData<Output>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getOutputUseCase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun observeTotalOutputsCount() {
        viewModelScope.launch {
            _searchQuery.collectLatest { query ->
                _totalOutputsCount.value = when (val result = getAllOutputsUseCase(query)) {
                    is Result.Success -> result.data.size
                    else -> 0
                }
            }
        }
    }

    fun deleteOutput(id: Int) {
        _deleteOutputUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteOutputUseCase(id)) {
                is Result.Success -> {
                    _totalOutputsCount.value = (_totalOutputsCount.value - 1).coerceAtLeast(0)
                    _deleteOutputUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteOutputUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }

    fun exportOutputsToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllOutputsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val outputs = result.data
                    if (outputs.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_outputs_to_export)
                        )
                        return@launch
                    }
                    try {
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateOutputListPdfUseCase(activityContext, outputs, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "outputs-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
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

    fun exportOutputsToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            when (val result = getAllOutputsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val outputs = result.data
                    if (outputs.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_outputs_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateOutputListExcelUseCase(activityContext, outputs, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "outputs-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
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
        _deleteOutputUiState.value = FormUIState.Idle
    }
}
