package com.groupec.feature.salelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
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
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.sale.GetSaleUseCase
import com.groupec.salesb.core.domain.sale.SaveSaleUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleListViewModel @Inject constructor(
    private val getSaleUseCase:GetSaleUseCase,
    private val getParameterUseCase: GetParameterUseCase,
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
            _isSearching.value = true // Indique qu'une recherche commence
            getSaleUseCase(searchParams)
                .onCompletion {
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

}
