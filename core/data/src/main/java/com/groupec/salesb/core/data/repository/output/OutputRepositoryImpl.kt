package com.groupec.salesb.core.data.repository.category

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toOutputList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OutputRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    OutputRepository {
    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override fun getOutputs(searchQuery: String) : Flow<PagingData<Output>> {
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
                        OutputPagingSource(apiService, searchQuery, userId)
                    }
                ).flow
            )
        }
    }

    override suspend fun getAllOutputs(searchQuery: String): Result<List<Output>> {
        return try {
            val response = apiService.getOutputs(searchQuery, currentUserId())
            if (!response.isSuccessful) {
                return Result.Error(HttpException(response))
            }
            val outputs = response.body()?.toOutputList().orEmpty()
            Result.Success(outputs)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveOutput(output: Output) : Result<Unit> {
        val outputValue = output.copy(
            userid = currentUserId()
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
