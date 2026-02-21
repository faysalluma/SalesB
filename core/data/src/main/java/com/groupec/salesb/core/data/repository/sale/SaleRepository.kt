package com.groupec.salesb.core.data.repository.sale

import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Sale
import kotlinx.coroutines.flow.Flow

interface SaleRepository {
    suspend fun saveSale(sale: Sale) : Result<Sale>
    fun getPagedSales(searchParams: Map<String, String>) : Flow<PagingData<Sale>>
    suspend fun getAllSales(searchParams: Map<String, String>) : Result<List<Sale>>
}
