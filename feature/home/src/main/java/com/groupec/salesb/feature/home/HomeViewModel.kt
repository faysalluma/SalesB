package com.groupec.salesb.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.domain.activity.GetRecentActivitiesUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.product.GetAlertSeuilProductUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalAmountSaleUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.RecentActivity
import com.groupec.salesb.core.model.data.UserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserStoreUseCase: GetUserStoreUseCase,
    private val getParameterUseCase: GetParameterUseCase,
    private val getTotalSaleUseCase: GetTotalSaleUseCase,
    private val getTotalAmountSaleUseCase: GetTotalAmountSaleUseCase,
    private val getAlertSeuilProductUseCase: GetAlertSeuilProductUseCase,
    private val getRecentActivitiesUseCase: GetRecentActivitiesUseCase,
) : ViewModel() {
    private val _userStore = MutableStateFlow(UserStore())
    val userStore: StateFlow<UserStore> = _userStore.asStateFlow()

    private val _parameter = MutableStateFlow(Parameter())
    val parameter: StateFlow<Parameter> = _parameter.asStateFlow()

    private val _totalSales = MutableStateFlow(0)
    val totalSales: StateFlow<Int> = _totalSales.asStateFlow()

    private val _totalAmountSales = MutableStateFlow(0.0)
    val totalAmountSales: StateFlow<Double> = _totalAmountSales.asStateFlow()

    private val _totalAlertSeuilProducts = MutableStateFlow(0)
    val totalAlertSeuilProducts: StateFlow<Int> = _totalAlertSeuilProducts.asStateFlow()

    private val _recentActivitiesUiState =
        MutableStateFlow<UIState<List<RecentActivity>>>(UIState.Loading)
    val recentActivitiesUiState: StateFlow<UIState<List<RecentActivity>>> =
        _recentActivitiesUiState.asStateFlow()

    private val _selectedPeriod = MutableStateFlow(Period.Today)
    val selectedPeriod: StateFlow<Period> = _selectedPeriod.asStateFlow()

    init {
        viewModelScope.launch {
            getUserStoreUseCase().collectLatest { userStore ->
                _userStore.value = userStore
            }
        }
        viewModelScope.launch {
            getParameterUseCase().collectLatest { parameter ->
                _parameter.value = parameter
            }
        }
    }

    fun onPeriodChange(period: Period) {
        _selectedPeriod.value = period
    }

    fun refreshDashboard(recentActivitiesLimit: Int) {
        val period = _selectedPeriod.value
        val (startDate, endDate) = period.dateRange()

        viewModelScope.launch {
            _totalSales.value = getTotalSaleUseCase(startDate, endDate).first()
            _totalAmountSales.value =
                getTotalAmountSaleUseCase(startDate, endDate).first()
        }
        viewModelScope.launch {
            _totalAlertSeuilProducts.value = getAlertSeuilProductUseCase().first()
        }
        viewModelScope.launch {
            _recentActivitiesUiState.value = UIState.Loading
            _recentActivitiesUiState.value = when (
                val result = getRecentActivitiesUseCase(
                    limit = recentActivitiesLimit,
                    startDate = startDate,
                    endDate = endDate,
                )
            ) {
                is Result.Success -> UIState.Success(result.data)
                is Result.Error -> UIState.Error(result.exception.message.orEmpty())
                Result.Loading -> UIState.Loading
            }
        }
    }
}
