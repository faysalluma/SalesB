package com.groupec.salesb.core.model.data

import java.util.Date

data class RecentActivity(
    val id: Int?,
    val type: RecentActivityType,
    val occurredAt: Date?,
    val amount: Double,
    val description: String? = null,
)

enum class RecentActivityType {
    Sale,
    Output,
}
