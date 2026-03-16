package com.groupec.salesb.core.data.repository.rayon

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toRayonList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RayonRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    RayonRepository {
    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override fun getRayons(searchQuery: String): Flow<List<Rayon>> = flow {
        val userId = currentUserId()
        val result = safeApiCall(
            apiCall = { apiService.getRayons(searchQuery, userId) },
            transform = { response ->
                response.toRayonList()
            }
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override suspend fun saveRayon(rayon: Rayon): Result<Unit> {
        val rayonValue = rayon.copy(
            userid = currentUserId()
        )
        return executeApiCall(
            apiCall = {
                apiService.addRayon(rayonValue)
            }
        )
    }

    override suspend fun deleteRayon(rayonId: Int): Result<Unit> {
        val result  = safeApiCall(
            apiCall = { apiService.deleteRayon(rayonId) },
            transform = {
                Result.Success(Unit)
            }
        )
        return result
    }

}
