package com.groupec.salesb.core.domain.activity

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.activity.RecentActivityRepository
import com.groupec.salesb.core.model.data.RecentActivity
import com.groupec.salesb.core.model.data.RecentActivityType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetRecentActivitiesUseCaseTest {
    private val repository: RecentActivityRepository = mockk()
    private val useCase = GetRecentActivitiesUseCase(repository)

    @Test
    fun `use case requests the given number of recent activities from repository`() = runTest {
        val activities = listOf(
            RecentActivity(
                id = 1,
                type = RecentActivityType.Sale,
                occurredAt = null,
                amount = 25.0,
            ),
        )
        coEvery {
            repository.getRecentActivities(
                limit = 5,
                startDate = "2026-08-15",
                endDate = "2026-08-15",
            )
        } returns Result.Success(activities)

        assertEquals(
            Result.Success(activities),
            useCase(limit = 5, startDate = "2026-08-15", endDate = "2026-08-15"),
        )
        coVerify(exactly = 1) {
            repository.getRecentActivities(
                limit = 5,
                startDate = "2026-08-15",
                endDate = "2026-08-15",
            )
        }
    }
}
