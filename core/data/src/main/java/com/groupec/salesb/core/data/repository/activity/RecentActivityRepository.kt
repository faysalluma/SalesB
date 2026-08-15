package com.groupec.salesb.core.data.repository.activity

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.RecentActivity

interface RecentActivityRepository {
    suspend fun getRecentActivities(
        limit: Int,
        startDate: String,
        endDate: String,
    ): Result<List<RecentActivity>>
}
