package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ProductResponse(
    @SerializedName("products")
    val products : ArrayList<ProductItemResponse>
)
data class ProductItemResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("libelle")
    val libelle: String,

    @SerializedName("desc")
    val desc: String,
)