package com.groupec.salesb.core.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDate
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : SaleRepository {
    override suspend fun saveSale(sale: Sale): Result<Unit> {
        val saleValue = sale.copy(
            datevente = currentDate(),
            datemodif = currentDateString(),
            userid = dataStoreManager.userFlow.first().id.toInt()
        )
        return executeApiCall(
            apiCall = {
                apiService.addSale(saleValue)
            }
        )
    }

    override fun getPagedProducts(searchParams: Map<String, String>): Flow<PagingData<Sale>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                SalePagingSource(apiService, searchParams)
            }
        ).flow
    }
}