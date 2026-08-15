package com.groupec.salesb.core.data.repository.activity

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toRecentActivityList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.RecentActivityItemResponse
import com.groupec.salesb.core.network.model.RecentActivityResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.util.Date

class RecentActivityRepositoryImplTest {
    private val apiService: ApiService = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private lateinit var repository: RecentActivityRepository

    @BeforeEach
    fun setup() {
        every { dataStoreManager.userFlow } returns flowOf(UserStore(id = "12"))
        repository = RecentActivityRepositoryImpl(apiService, dataStoreManager)
    }

    @Test
    fun `repository requests limited activities for current user`() = runTest {
        val response = RecentActivityResponse(
            activities = listOf(
                RecentActivityItemResponse(
                    id = 8,
                    type = "sale",
                    date = Date(1_000L),
                    amount = 150.0,
                ),
            ),
        )
        coEvery {
            apiService.getRecentActivities(
                limit = 3,
                startDate = "2026-08-01",
                endDate = "2026-08-15",
                userid = 12,
            )
        } returns
            Response.success(response)

        assertEquals(
            Result.Success(response.toRecentActivityList()),
            repository.getRecentActivities(
                limit = 3,
                startDate = "2026-08-01",
                endDate = "2026-08-15",
            ),
        )
        coVerify(exactly = 1) {
            apiService.getRecentActivities(
                limit = 3,
                startDate = "2026-08-01",
                endDate = "2026-08-15",
                userid = 12,
            )
        }
    }

    @Test
    fun `repository returns error when api response fails`() = runTest {
        coEvery {
            apiService.getRecentActivities(
                limit = 3,
                startDate = "2026-08-01",
                endDate = "2026-08-15",
                userid = 12,
            )
        } returns Response.error(
            500,
            "server error".toResponseBody("text/plain".toMediaType()),
        )

        val result = repository.getRecentActivities(
            limit = 3,
            startDate = "2026-08-01",
            endDate = "2026-08-15",
        )

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }
}
