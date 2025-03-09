package com.groupec.feature.sale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.product.DeleteProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.groupec.salesb.core.domain.product.GetProductUseCase
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.sale.SaveSaleUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val saveSaleUseCase: SaveSaleUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _parameter = MutableStateFlow(Parameter())
    val parameter : StateFlow<Parameter> = _parameter.asStateFlow()

    private val _addSaleUiState = MutableStateFlow<FormUIState<*>>(FormUIState.Idle)
    val addSaleUiState : StateFlow<FormUIState<*>> = _addSaleUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().first()
        }
    }

    val pagedProducts: Flow<PagingData<Product>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getProductUseCase(query)
                .map { pagingData ->
                    pagingData.filter { product ->
                        product.qtestock?.let { it > 0 } ?: true // Keep product with qtestock>0 or null
                    }
                }
                .onCompletion {
                    _isSearching.value = false
                } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun addSale(sale: Sale) {
        _addSaleUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = saveSaleUseCase(sale)) {
                is Result.Success -> {
                    _addSaleUiState.value = FormUIState.Success(Unit)
                }

                is Result.Error -> {
                    _addSaleUiState.value = FormUIState.Error(result.exception.message ?: "Error when adding product")
                }
                else -> {}
            }
        }
    }

    fun resetFlow() {
        _addSaleUiState.value = FormUIState.Idle
    }

}
