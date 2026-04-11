package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class ClientResponse(
    @SerializedName("clients")
    val clients : ArrayList<ClientItemResponse>
)
data class ClientItemResponse(
    @SerializedName("id")
    val id: Int ? = null,

    @SerializedName("datecreation")
    val datecreation: Date? = null,

    @SerializedName("nomprenom")
    val nomprenom: String,

    @SerializedName("adresse")
    val adresse: String? = null,

    @SerializedName("telephone")
    val telephone: String? = null,

    @SerializedName("datemodif")
    val datemodif: Date ? = null,

    @SerializedName("userid")
    val userid: Int ? = null,

    @SerializedName("user")
    val user: UserReducedResponse? = null
)

data class ClientReducedResponse(
    @SerializedName("id")
    val id: Int ,

    @SerializedName("nomprenom")
    val nomprenom: String,

    @SerializedName("adresse")
    val adresse: String? = null,

    @SerializedName("telephone")
    val telephone: String? = null
)
