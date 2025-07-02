package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OutputResponse(
    @SerializedName("outputs")
    val outputs : ArrayList<OutputItemResponse>
)
data class OutputItemResponse(
    @SerializedName("id")
    val id: Int ? = null,

    @SerializedName("datecreation")
    val datecreation: Date ? = null,

    @SerializedName("prix")
    val prix: Double,

    @SerializedName("description")
    val description: String,

    @SerializedName("datemodif")
    val datemodif: Date ? = null,

    @SerializedName("user")
    val user: UserReducedResponse ? = null
)