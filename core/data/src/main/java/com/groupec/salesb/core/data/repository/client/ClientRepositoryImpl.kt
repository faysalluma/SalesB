package com.groupec.salesb.core.data.repository.client

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toClientList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ClientRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ClientRepository {

    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override fun getClients(): Flow<List<Client>> = flow {
        val userId = currentUserId()
        val result = safeApiCall(
            apiCall = { apiService.getClients(userId) },
            transform = { response ->
                response.toClientList()
            }
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllClients(searchQuery: String): Result<List<Client>> {
        return try {
            val response = apiService.getAllClients(searchQuery, currentUserId())
            if (!response.isSuccessful) {
                return Result.Error(retrofit2.HttpException(response))
            }
            Result.Success(response.body()?.toClientList().orEmpty())
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override fun getPagedClients(searchQuery: String): Flow<PagingData<Client>> {
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
                        ClientPagingSource(apiService, searchQuery, userId)
                    }
                ).flow
            )
        }
    }

    override suspend fun saveClient(client: Client): Result<Unit> {
        val clientValue = client.copy(userid = currentUserId())
        return executeApiCall(
            apiCall = {
                apiService.addClient(clientValue)
            }
        )
    }

    override suspend fun deleteClient(clientId: Int): Result<Unit> {
        return safeApiCall(
            apiCall = { apiService.deleteClient(clientId) },
            transform = {
                Result.Success(Unit)
            }
        )
    }
}
