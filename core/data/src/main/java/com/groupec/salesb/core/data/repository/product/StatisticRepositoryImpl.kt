package com.groupec.salesb.core.data.repository.product

import com.groupec.salesb.core.data.model.toProductList
import com.groupec.salesb.core.dayMonth
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.safeApiCallGetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StatisticRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : StatisticRepository {
    override fun getTotalSales(startDate: String, endDate: String): Flow<Int> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getTotalSales(startDate, endDate) },
            transform = { response ->
                response.data ?: 0
            },
            default = 0
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getTotalAmountSales(startDate: String, endDate: String): Flow<Double> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getTotalAmountSales(startDate, endDate) },
            transform = { response ->
                response.data ?: 0.0
            },
            default = 0.0
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getTotalProducts(): Flow<Int> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getTotalProducts() },
            transform = { response ->
                response.data ?: 0
            },
            default = 0
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getTopSaleProducts(startDate: String, endDate: String): Flow<List<Product>> =
        flow {
            val result = safeApiCallGetResult(
                apiCall = { apiService.getTopSaleProducts(startDate, endDate) },
                transform = { response ->
                    response.toProductList()
                },
                default = emptyList()
            )
            emit(result)
        }.flowOn(Dispatchers.IO)

    override fun getAlertSeuil(): Flow<Int> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getAlertSeuil() },
            transform = { response ->
                response.data ?: 0
            },
            default = 0
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getTotalSaleMorningEvening(date: String): Flow<Pair<Double, Double>> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getTotalSaleMorningEvening(date) },
            transform = { response ->
                response.data?.let {
                    Pair(it.totalsalemorning, it.totalsalevening)
                } ?: Pair(0.0, 0.0)
            },
            default = Pair(0.0, 0.0)
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getTotalSalesByDate(
        startDate: String,
        endDate: String
    ): Flow<List<Pair<String, Double>>> = flow {
        val result = safeApiCallGetResult(
            apiCall = { apiService.getTotalSalesByDate(startDate, endDate) },
            transform = { response ->
                response.sales.map { Pair(it.datevente.dayMonth(), it.totalprix) }
            },
            default = emptyList()
        )
        emit(result)
    }.flowOn(Dispatchers.IO)
}