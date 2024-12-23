package com.groupec.feature.productlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.model.data.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.groupec.salesb.core.domain.product.GetProductUseCase
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
   private val getProductUseCase: GetProductUseCase
) : ViewModel() {

     val searchQuery = MutableStateFlow("")

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    val pagedProducts: Flow<PagingData<Product>> = searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getProductUseCase(query)
                .onCompletion { _isSearching.value = false } // Recherche terminée
        }
        .cachedIn(viewModelScope) // Cache les données pour le cycle de vie de l'UI

    fun updateSearchQuery(newQuery: String) {
        searchQuery.value = newQuery
    }

    fun deleteProduct(id: Int) {

    }
}
