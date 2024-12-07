package com.groupec.salesb.feature.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.statistic.GetTotalAmountSaleUseCase
import com.groupec.salesb.core.domain.product.GetTotalProductUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.product.GetAlertSeuilProductUseCase
import com.groupec.salesb.core.domain.product.GetTopSaleProductsUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val getTotalSaleUseCase: GetTotalSaleUseCase,
    private val getTotalAmountSaleUseCase: GetTotalAmountSaleUseCase,
    private val getTotalProductUseCase: GetTotalProductUseCase,
    private val getTopSaleProductsUseCase: GetTopSaleProductsUseCase,
    private val getAlertSeuilProductUseCase: GetAlertSeuilProductUseCase

) : ViewModel() {
    private val _userStore = MutableStateFlow(UserStore())
    val userStore : StateFlow<UserStore> = _userStore.asStateFlow()

    private val _parameter = MutableStateFlow(Parameter())
    val parameter : StateFlow<Parameter> = _parameter.asStateFlow()

    private val _totalSales = MutableStateFlow(0)
    val totalSales : StateFlow<Int> = _totalSales.asStateFlow()

    private val _totalProducts = MutableStateFlow(0)
    val totalProducts : StateFlow<Int> = _totalProducts.asStateFlow()

    private val _totalAmountSales = MutableStateFlow(0)
    val totalAmountSales : StateFlow<Int> = _totalAmountSales.asStateFlow()

    private val _topSaleProducts = MutableStateFlow<List<Product>>(emptyList())
    val topSaleProducts : StateFlow<List<Product>> = _topSaleProducts.asStateFlow()

    private val _totalAlertSeuilProducts = MutableStateFlow(0)
    val totalAlertSeuilProducts : StateFlow<Int> = _totalAlertSeuilProducts.asStateFlow()

    init {
        viewModelScope.launch {
            _userStore.value = getUserStoreUseCase().first()
            _parameter.value = getParameterUseCase().first()
        }
    }

    fun getTotalSale(startDate: String, endDate: String) {
        viewModelScope.launch {
            _totalSales.value = getTotalSaleUseCase(startDate, endDate).first()
            _totalAmountSales.value = getTotalAmountSaleUseCase(startDate, endDate).first()
        }
    }

    fun getTotalProduct() {
        viewModelScope.launch {
            _totalProducts.value = getTotalProductUseCase().first()
        }
    }

    fun getTopSaleProducts(startDate: String, endDate: String) {
        viewModelScope.launch {
            _topSaleProducts.value = getTopSaleProductsUseCase(startDate, endDate).first()
        }
    }

    fun getTotalAlertSeuil() {
        viewModelScope.launch {
            _totalAlertSeuilProducts.value = getAlertSeuilProductUseCase().first()
        }
    }
}
