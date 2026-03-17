package com.groupec.salesb.core.data.repository.sale

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toSale
import com.groupec.salesb.core.data.model.toSaleList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : SaleRepository {
    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override suspend fun saveSale(sale: Sale): Result<Sale> {
        return try {
            val saleValue = sale.copy(
                userid = currentUserId()
            )
            val response =  apiService.addSale(saleValue)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    val sales = result.data!!.toSale()
                    Result.Success(sales)
                } ?: Result.Error(Exception(context.getString(R.string.error_empty_response)))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getPagedSales(searchParams: Map<String, String>): Flow<PagingData<Sale>> {
        return flow {
            val userId = currentUserId()
            emitAll(
                Pager(
                    config = PagingConfig(
                        pageSize = 15,
                        initialLoadSize = 15,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        SalePagingSource(apiService, searchParams, userId)
                    }
                ).flow
            )
        }
    }

    override suspend fun getAllSales(searchParams: Map<String, String>): Result<List<Sale>> {
        return try {
            val response = apiService.getSales(searchParams, currentUserId())
            if (!response.isSuccessful) {
                return Result.Error(HttpException(response))
            }
            val sales = response.body()?.toSaleList().orEmpty()
            Result.Success(sales)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
