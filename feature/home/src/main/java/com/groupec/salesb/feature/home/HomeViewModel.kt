package com.groupec.salesb.feature.home


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.asResult
import com.groupec.salesb.core.domain.statistic.GetTotalAmountSaleUseCase
import com.groupec.salesb.core.domain.product.GetTotalProductUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.product.GetAlertSeuilProductUseCase
import com.groupec.salesb.core.domain.product.GetProductsWithLowInventoryUseCase
import com.groupec.salesb.core.domain.product.GetTopSaleProductsUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalAmountOutputUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalSaleByDateUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalSaleDayUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.groupec.salesb.core.Result
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val getTotalSaleUseCase: GetTotalSaleUseCase,
    private val getTotalProductUseCase: GetTotalProductUseCase,
    private val getTopSaleProductsUseCase: GetTopSaleProductsUseCase,
    private val getAlertSeuilProductUseCase: GetAlertSeuilProductUseCase,
    private val getTotalSaleDayUseCase: GetTotalSaleDayUseCase,
    private val getTotalSaleByDateUseCase: GetTotalSaleByDateUseCase,
    private val getTotalAmountSaleUseCase: GetTotalAmountSaleUseCase,
    private val getTotalAmountOutputUseCase: GetTotalAmountOutputUseCase,
    private val getProductsWithLowInventoryUseCase: GetProductsWithLowInventoryUseCase
) : ViewModel() {
    private val _userStore = MutableStateFlow(UserStore())
    val userStore : StateFlow<UserStore> = _userStore.asStateFlow()

    private val _parameter = MutableStateFlow(Parameter())
    val parameter : StateFlow<Parameter> = _parameter.asStateFlow()

    private val _totalSales = MutableStateFlow(0)
    val totalSales : StateFlow<Int> = _totalSales.asStateFlow()

    private val _totalProducts = MutableStateFlow(0)
    val totalProducts : StateFlow<Int> = _totalProducts.asStateFlow()

    private val _totalAmountSales = MutableStateFlow(0.0)
    val totalAmountSales : StateFlow<Double> = _totalAmountSales.asStateFlow()

    private val _totalAmountOutputs = MutableStateFlow(0.0)
    val totalAmountOutputs : StateFlow<Double> = _totalAmountOutputs.asStateFlow()

    private val _topSaleProducts = MutableStateFlow<List<Product>>(emptyList())
    val topSaleProducts : StateFlow<List<Product>> = _topSaleProducts.asStateFlow()

    private val _totalAlertSeuilProducts = MutableStateFlow(0)
    val totalAlertSeuilProducts : StateFlow<Int> = _totalAlertSeuilProducts.asStateFlow()

    private val _totalSaleChartDay = MutableStateFlow(Pair(0.0, 0.0))
    val totalSaleChartDay : StateFlow<Pair<Double, Double>> = _totalSaleChartDay.asStateFlow()

    private val _chartValues = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val chartValues : StateFlow<List<Pair<String, Double>>> = _chartValues.asStateFlow()

    private val _productsWithLowInventoryUiState = MutableStateFlow<UIState<List<Product>>>(UIState.Loading)
    val productsWithLowInventoryUiState: StateFlow<UIState<List<Product>>> = _productsWithLowInventoryUiState.asStateFlow()

    init {
        viewModelScope.launch {
            _userStore.value = getUserStoreUseCase().first()
            _parameter.value = getParameterUseCase().first()
        }
    }

    fun getProductsWithLowInventory() {
        viewModelScope.launch {
            getProductsWithLowInventoryUseCase()
                .asResult()
                .collect { result ->
                    _productsWithLowInventoryUiState.value = when (result) {
                        is Result.Loading-> UIState.Loading
                        is Result.Success -> UIState.Success(result.data)
                        is Result.Error -> UIState.Error(
                            result.exception.message ?: "Retrofit Unknown error"
                        )
                    }

                }
        }
    }

    fun getTotalSale(startDate: String, endDate: String) {
        viewModelScope.launch {
            _totalSales.value = getTotalSaleUseCase(startDate, endDate).first()
            _totalAmountSales.value = getTotalAmountSaleUseCase(startDate, endDate).first()
            _totalAmountOutputs.value = getTotalAmountOutputUseCase(startDate, endDate).first()
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

    fun getChartDataToday(context: Context, date: String) {
        viewModelScope.launch {
           // _totalSaleChartDay.value = getTotalSaleDayUseCase(date).first()

            // Update chart
            val (totalSalesMorning, totalSalesEvening) = getTotalSaleDayUseCase(date).first()
            _chartValues.value = listOf(
                context.getString(R.string.morning) to totalSalesMorning,
                context.getString(R.string.evening) to totalSalesEvening
            )
        }
    }
    fun getChartDataByDate(startDate: String, endDate: String) {
        viewModelScope.launch {
            _chartValues.value = getTotalSaleByDateUseCase(startDate, endDate).first()
        }
    }

}
