package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ChartDateResponse(
    @SerializedName("sales")
    val sales: ArrayList<ChartDateItemResponse>
)

data class ChartDateItemResponse(
    @SerializedName("datevente")
    val datevente: Date,

    @SerializedName("totalprix")
    val totalprix: Double
)
