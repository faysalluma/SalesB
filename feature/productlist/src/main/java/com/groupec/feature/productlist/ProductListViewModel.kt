package com.groupec.feature.productlist

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.feature.product.R
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.product.DeleteProductUseCase
import com.groupec.salesb.core.domain.product.GenerateProductListPdfUseCase
import com.groupec.salesb.core.domain.product.GetAllProductsUseCase
import com.groupec.salesb.core.domain.product.GetProductUseCase
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.savePdfToDownloads
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val getAllProductsUseCase: GetAllProductsUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    private val generateProductListPdfUseCase: GenerateProductListPdfUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteProductUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val deleteProductUiState: StateFlow<FormUIState<*>> = _deleteProductUiState.asStateFlow()

    private val _exportPdfUiState = MutableStateFlow<FormUIState<File>>(FormUIState.Idle)
    val exportPdfUiState: StateFlow<FormUIState<File>> = _exportPdfUiState.asStateFlow()

    val pagedProducts: Flow<PagingData<Product>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getProductUseCase(query)
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deleteProduct(id: Int) {
        _deleteProductUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = deleteProductUseCase(id)) {
                is Result.Success -> {
                    _deleteProductUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _deleteProductUiState.value =
                        FormUIState.Error(result.exception.message ?: "Error when deleting product")
                }

                else -> {}
            }
        }
    }

    fun exportProductsToPdf(activityContext: Context) {
        viewModelScope.launch {
            _exportPdfUiState.value = FormUIState.Loading
            when (val result = getAllProductsUseCase(_searchQuery.value)) {
                is Result.Success -> {
                    val products = result.data
                    if (products.isEmpty()) {
                        _exportPdfUiState.value = FormUIState.Error(
                            activityContext.getString(R.string.no_products_to_export)
                        )
                        return@launch
                    }
                    try {
                        val pdfBytes = withContext(Dispatchers.Default) {
                            generateProductListPdfUseCase(
                                activityContext,
                                products,
                                _searchQuery.value
                            )
                        }
                        val file = withContext(Dispatchers.IO) {
                            activityContext.savePdfToDownloads(pdfBytes, "products.pdf")
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

    fun resetExportState() {
        _exportPdfUiState.value = FormUIState.Idle
    }

    fun resetFlow() {
        _deleteProductUiState.value = FormUIState.Idle
    }

}
