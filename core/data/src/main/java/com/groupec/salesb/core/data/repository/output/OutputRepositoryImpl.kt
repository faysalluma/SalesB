package com.groupec.salesb.core.data.repository.category

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutputRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    OutputRepository {
    override fun getOutputs(searchQuery: String) : Flow<PagingData<Output>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                OutputPagingSource(apiService, searchQuery)
            }
        ).flow
    }

    override suspend fun saveOutput(output: Output) : Result<Unit> {
        val outputValue = output.copy(
            userid = dataStoreManager.userFlow.first().id.toInt()
        )
        return executeApiCall(
            apiCall = {
                apiService.addOutput(outputValue)
            }
        )
    }

    override suspend fun deleteOutput(outputId: Int) : Result<Unit> {
        val result  = safeApiCall(
            apiCall = { apiService.deleteOutput(outputId) },
            transform = {
                Result.Success(Unit)
            }
        )
        return result
    }

}