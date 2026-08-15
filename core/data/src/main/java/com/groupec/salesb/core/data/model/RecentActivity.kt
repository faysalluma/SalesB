package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.RecentActivity
import com.groupec.salesb.core.model.data.RecentActivityType
import com.groupec.salesb.core.network.model.RecentActivityItemResponse
import com.groupec.salesb.core.network.model.RecentActivityResponse
import java.util.Locale

fun RecentActivityResponse.toRecentActivityList(): List<RecentActivity> =
    activities.map(RecentActivityItemResponse::toRecentActivity)

private fun RecentActivityItemResponse.toRecentActivity() = RecentActivity(
    id = id,
    type = when (type.lowercase(Locale.ROOT)) {
        "sale" -> RecentActivityType.Sale
        "output" -> RecentActivityType.Output
        else -> throw IllegalArgumentException("Unknown recent activity type: $type")
    },
    occurredAt = date,
    amount = amount,
)
