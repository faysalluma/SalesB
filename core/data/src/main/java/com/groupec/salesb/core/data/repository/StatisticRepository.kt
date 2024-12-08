package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow

interface StatisticRepository {
    fun getTotalSales(startDate: String, endDate: String) : Flow<Int>
    fun getTotalAmountSales(startDate: String, endDate: String) : Flow<Int>
    fun getTotalProducts() : Flow<Int>
    fun getTopSaleProducts(startDate: String, endDate: String) : Flow<List<Product>>
    fun getAlertSeuil() : Flow<Int>
    fun getTotalSaleMorningEvening(date: String) : Flow<Pair<Double, Double>>
    fun getTotalSalesByDate(startDate: String, endDate: String): Flow<List<Pair<String, Double>>>
}