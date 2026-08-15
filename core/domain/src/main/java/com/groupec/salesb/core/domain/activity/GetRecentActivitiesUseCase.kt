package com.groupec.salesb.core.domain.activity

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.activity.RecentActivityRepository
import com.groupec.salesb.core.model.data.RecentActivity
import javax.inject.Inject

class GetRecentActivitiesUseCase @Inject constructor(
    private val recentActivityRepository: RecentActivityRepository,
) {
    suspend operator fun invoke(
        limit: Int,
        startDate: String,
        endDate: String,
    ): Result<List<RecentActivity>> = recentActivityRepository.getRecentActivities(
        limit = limit,
        startDate = startDate,
        endDate = endDate,
    )
}
