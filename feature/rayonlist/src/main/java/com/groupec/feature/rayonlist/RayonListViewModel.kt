package com.groupec.feature.rayonlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.asResult
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.rayon.DeleteRayonUseCase
import com.groupec.salesb.core.domain.rayon.GenerateRayonListExcelUseCase
import com.groupec.salesb.core.domain.rayon.GenerateRayonListPdfUseCase
import com.groupec.salesb.core.domain.rayon.GetAllRayonsUseCase
import com.groupec.salesb.core.domain.rayon.GetRayonUseCase
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class RayonListViewModel @Inject constructor(
    private val getRayonUseCase: GetRayonUseCase,
    private val deleteRayonUseCase: DeleteRayonUseCase,
    private val getAllRayonsUseCase: GetAllRayonsUseCase,
    private val generateRayonListPdfUseCase: GenerateRayonListPdfUseCase,
    private val generateRayonListExcelUseCase: GenerateRayonListExcelUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _rayonUiState = MutableStateFlow<RayonUiState>(RayonUiState.Loading)
    val rayonUiState: StateFlow<RayonUiState> = _rayonUiState.asStateFlow()

    private val _deleteRayonUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteRayonUiState: StateFlow<FormUIState<*>> = _deleteRayonUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()

    init {
        getRayons()
    }

    fun getRayons() {
        viewModelScope.launch {
            _searchQuery.flatMapLatest { query ->
                getRayonUseCase(query)
            }
                .asResult()
                .collect { result ->
                    _rayonUiState.value = when (result) {
                        is Result.Loading-> RayonUiState.Loading
                        is Result.Success -> {
                            if (result.data.isEmpty()){
                                RayonUiState.Empty
                            } else {
                                RayonUiState.Success(result.data)
                            }
                        }
                        is Result.Error -> RayonUiState.Error( result.exception.message ?: "Retrofit Unknown error")
                    }
                }
        }
    }

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteRayon(id: Int) {
        _deleteRayonUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteRayonUseCase(id)) {
                is Result.Success -> {
                    _deleteRayonUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteRayonUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }

    fun exportRayonsToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllRayonsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val rayons = result.data
                    if (rayons.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_rayons_to_export)
                        )
                        return@launch
                    }
                    try {
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateRayonListPdfUseCase(activityContext, rayons, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "rayons-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
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

    fun exportRayonsToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            when (val result = getAllRayonsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val rayons = result.data
                    if (rayons.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_rayons_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateRayonListExcelUseCase(activityContext, rayons, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "rayons-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
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
        _deleteRayonUiState.value = FormUIState.Idle
    }
}

sealed class RayonUiState {
    data object Loading : RayonUiState()
    data class Success(val rayons: List<Rayon>) : RayonUiState()
    data class Error(val message: String) : RayonUiState()
    data object Empty : RayonUiState()
}
