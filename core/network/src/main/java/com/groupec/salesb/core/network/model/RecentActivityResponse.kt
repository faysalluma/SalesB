package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class RecentActivityResponse(
    @SerializedName("activities")
    val activities: List<RecentActivityItemResponse>,
)

data class RecentActivityItemResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("type")
    val type: String,
    @SerializedName("date")
    val date: Date,
    @SerializedName("amount")
    val amount: Double,
)
