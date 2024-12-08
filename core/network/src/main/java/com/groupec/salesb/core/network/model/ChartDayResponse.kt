package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class ChartDayResponse(
    @SerializedName("totalsalemorning")
    val totalsalemorning: Double,

    @SerializedName("totalsalevening")
    val totalsalevening: Double
)
