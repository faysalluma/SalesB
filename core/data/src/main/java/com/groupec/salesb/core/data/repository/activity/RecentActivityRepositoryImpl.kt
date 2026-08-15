package com.groupec.salesb.core.data.repository.activity

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toRecentActivityList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.RecentActivity
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.flow.firstOrNull
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentActivityRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
) : RecentActivityRepository {
    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override suspend fun getRecentActivities(
        limit: Int,
        startDate: String,
        endDate: String,
    ): Result<List<RecentActivity>> {
        return try {
            val response = apiService.getRecentActivities(
                limit = limit,
                startDate = startDate,
                endDate = endDate,
                userid = currentUserId(),
            )
            if (!response.isSuccessful) {
                return Result.Error(HttpException(response))
            }
            Result.Success(response.body()?.toRecentActivityList().orEmpty())
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}
