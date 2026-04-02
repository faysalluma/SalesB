package com.groupec.feature.sale

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.asResult
import com.groupec.salesb.core.domain.client.GetClientUseCase
import com.groupec.salesb.core.print.PrintAction
import com.groupec.salesb.core.Result
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
import com.groupec.salesb.core.domain.sale.SaveSaleUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.getCurrentMontDelimitedDates
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class SaleViewModel @Inject constructor(
    private val getClientUseCase: GetClientUseCase,
    private val getProductUseCase: GetProductUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val getTotalSaleUseCase: GetTotalSaleUseCase,
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val saveSaleUseCase: SaveSaleUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching = MutableStateFlow(false) // État de recherche
    val isSearching: StateFlow<Boolean> = _isSearching

    private val _parameter = MutableStateFlow(Parameter())
    val parameter : StateFlow<Parameter> = _parameter.asStateFlow()

    private val _clientsUiPairState = MutableStateFlow<List<Pair<String, String>>>(emptyList())
    val clientsUiPairState: StateFlow<List<Pair<String, String>>> = _clientsUiPairState.asStateFlow()

    private val _userStoreState = MutableStateFlow(UserStore())
    val userStoreState: StateFlow<UserStore> = _userStoreState.asStateFlow()

    private val _addSaleUiState = MutableStateFlow<FormUIState<Pair<PrintAction, Sale>>>(FormUIState.Idle)
    val addSaleUiState : StateFlow<FormUIState<Pair<PrintAction, Sale>>> = _addSaleUiState.asStateFlow()

    init {
        observeUserStore()
    }

    private fun observeUserStore() {
        viewModelScope.launch {
            getUserStoreUseCase().collectLatest { userStore ->
                _userStoreState.value = userStore
            }
        }
    }

    fun getParameter() {
        viewModelScope.launch {
            _parameter.value = getParameterUseCase().first()
        }
    }

    fun getClients() {
        viewModelScope.launch {
            getClientUseCase()
                .asResult()
                .collect { result ->
                    when (result) {
                        is Result.Success -> _clientsUiPairState.value = result.data.mapNotNull {
                            it.id?.toString()?.let { clientId -> clientId to it.nomprenom }
                        }
                        else -> _clientsUiPairState.value = emptyList()
                    }
                }
        }
    }

    val pagedProducts: Flow<PagingData<Product>> = _searchQuery
        .flatMapLatest { query ->
            _isSearching.value = true // Indique qu'une recherche commence
            getProductUseCase(query)
                .map { pagingData ->
                    pagingData.filter { product ->
                        product.qtestock?.let { it > 0 } ?: true // Keep product with qtestock > 0 or null
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

    suspend fun hasReachedFreeMonthlySalesLimit(): Boolean {
        val (startDate, endDate) = getCurrentMontDelimitedDates()
        val monthlySalesCount = getTotalSaleUseCase(startDate, endDate).first()
        return monthlySalesCount > 100
    }

    fun addSale(sale: Sale, printAction: PrintAction) {
        _addSaleUiState.value = FormUIState.Loading
        viewModelScope.launch {
            when (val result = saveSaleUseCase(sale)) {
                is Result.Success -> {
                    _addSaleUiState.value = FormUIState.Success(Pair(printAction, result.data))
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
