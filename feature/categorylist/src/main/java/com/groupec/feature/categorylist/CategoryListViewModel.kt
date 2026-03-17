package com.groupec.feature.categorylist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.domain.category.DeleteCategoryUseCase
import com.groupec.salesb.core.domain.category.GenerateCategoryListExcelUseCase
import com.groupec.salesb.core.domain.category.GenerateCategoryListPdfUseCase
import com.groupec.salesb.core.domain.category.GetAllCategoriesUseCase
import com.groupec.salesb.core.domain.category.GetPagedCategoryUsecase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.saveExcelToDownloads
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class CategoryListViewModel @Inject constructor(
    private val getPagedCategoryUsecase: GetPagedCategoryUsecase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase,
    private val getAllCategoriesUseCase: GetAllCategoriesUseCase,
    private val generateCategoryListPdfUseCase: GenerateCategoryListPdfUseCase,
    private val generateCategoryListExcelUseCase: GenerateCategoryListExcelUseCase,
    private val getParameterUseCase: GetParameterUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteCategoryUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteCategoryUiState: StateFlow<FormUIState<*>> = _deleteCategoryUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    private val _exportExcelUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportExcelUiState: StateFlow<FormUIState<File>> = _exportExcelUiState.asStateFlow()

    val pagedCategories: Flow<PagingData<Category>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getPagedCategoryUsecase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteCategory(id: Int) {
        _deleteCategoryUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteCategoryUseCase(id)) {
                is Result.Success -> {
                    _deleteCategoryUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteCategoryUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting category")
                }

                else -> {}
            }
        }
    }

    fun exportCategoriesToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllCategoriesUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val categories = result.data
                    if (categories.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_categories_to_export)
                        )
                        return@launch
                    }
                    try {
                        val logoUrl = getParameterUseCase().first().logo
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateCategoryListPdfUseCase(
                                activityContext,
                                categories,
                                _searchQuery.value,
                                logoUrl
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(
                                pdfBytes,
                                "categories-${currentDateString("yyyy-MM-dd-HHmmss")}.pdf"
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

    fun exportCategoriesToExcel(activityContext: Context) {
        viewModelScope.launch {
            _exportExcelUiState.value = FormUIState.Loading
            when (val result = getAllCategoriesUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val categories = result.data
                    if (categories.isEmpty()) {
                        _exportExcelUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_categories_to_export)
                        )
                        return@launch
                    }
                    try {
                        val excelBytes = withContext(Dispatchers.Default) {
                            generateCategoryListExcelUseCase(activityContext, categories, _searchQuery.value)
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.saveExcelToDownloads(
                                excelBytes,
                                "categories-${currentDateString("yyyy-MM-dd-HHmmss")}.csv"
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
        _deleteCategoryUiState.value = FormUIState.Idle
    }
}
