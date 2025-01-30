package com.groupec.feature.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
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
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UIState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ProductListViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _deleteProductUiState = MutableStateFlow<UIState<*>>(UIState.Loading)
    val deleteProductUiState : StateFlow<UIState<*>> = _deleteProductUiState.asStateFlow()

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
        _deleteProductUiState.value = UIState.Loading
        viewModelScope.launch {
            when (val result = deleteProductUseCase(id)) {
                is Result.Success -> {
                    _deleteProductUiState.value = UIState.Success(Unit)
                }
                is Result.Error -> {
                    _deleteProductUiState.value = UIState.Error(result.exception.message ?: "Error when deleting product")
                }
                else -> {}
            }
        }
    }
}
