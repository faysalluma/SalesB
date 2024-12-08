package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class ChartDateResponse(
    @SerializedName("sales")
    val sales: ArrayList<ChartDateItemResponse>
)

data class ChartDateItemResponse(
    @SerializedName("datevente")
    val datevente: String,

    @SerializedName("totalprix")
    val totalprix: Double
)
