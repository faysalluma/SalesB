package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class CategoryResponse(
    @SerializedName("categories")
    val categories : ArrayList<CategoryItemResponse>
)
data class CategoryItemResponse(
    @SerializedName("id")
    val id: Int ? = null,

    @SerializedName("datecreation")
    val datecreation: Date ? = null,

    @SerializedName("libelle")
    val libelle: String,

    @SerializedName("description")
    val description: String ? = null,

    @SerializedName("datemodif")
    val datemodif: Date ? = null,

    @SerializedName("user")
    val user: UserReducedResponse ? = null
)