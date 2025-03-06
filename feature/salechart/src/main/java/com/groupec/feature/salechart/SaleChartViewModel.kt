package com.groupec.feature.salechart


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.statistic.GetTotalSaleByDateUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SaleChartViewModel @Inject constructor(
    private val getTotalSaleByDateUseCase: GetTotalSaleByDateUseCase

) : ViewModel() {

    private val _chartValues = MutableStateFlow<List<Pair<String, Double>>>(emptyList())
    val chartValues: StateFlow<List<Pair<String, Double>>> = _chartValues.asStateFlow()

    fun getChartDataByDate(startDate: String, endDate: String) {
        viewModelScope.launch {
            _chartValues.value = getTotalSaleByDateUseCase(startDate, endDate).first()
        }
    }

}
