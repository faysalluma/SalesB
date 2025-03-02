package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class SaleResponse(
    @SerializedName("sales")
    val sales : ArrayList<SaleItemResponse>
)
data class SaleItemResponse(
    @SerializedName("id")
    val id: Int ? = null,

    @SerializedName("datevente")
    val datevente: String ? = null,

    @SerializedName("totalreduc")
    val totalreduc: Double ? = null,

    @SerializedName("totalprix")
    val totalprix: Double,

    @SerializedName("client")
    val client: ClientReducedResponse ? = null,

    @SerializedName("datemodif")
    val datemodif: String ? = null,

    @SerializedName("user")
    val user: UserReducedResponse ? = null,

    @SerializedName("products")
    val products: ArrayList<ProductReducedResponse>,
)