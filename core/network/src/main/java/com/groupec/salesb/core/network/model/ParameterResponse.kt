package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class ParameterResponse(
    @SerializedName("parameter")
    val parameter : ParamItemResponse
)
data class ParamItemResponse(

    @SerializedName("userid")
    val id: Int,

    @SerializedName("logo")
    val logo: String?,

    @SerializedName("raisonsociale")
    val raisonsociale: String,


    @SerializedName("entreprisetype")
    val entreprisetype: Int,

    @SerializedName("ifu")
    val ifu: String?,

    @SerializedName("adresse")
    val adresse: String?,

    @SerializedName("telephone")
    val telephone: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("website")
    val website: String?,

    @SerializedName("devise")
    val devise: String,

    @SerializedName("tva")
    val tva: Double,

    @SerializedName("expirationdate")
    val expirationdate: String,

    @SerializedName("offline")
    val offline: Int,

    @SerializedName("showimageonproduct")
    val showimageonproduct: Int,

    @SerializedName("useintforpriceandamout")
    val useintforpriceandamout: Int,

    @SerializedName("activepaymentmode")
    val activepaymentmode: Int,

    @SerializedName("defaultpayment")
    val defaultpayment: String ? = null,

    @SerializedName("activeprinter")
    val activeprinter: Int
)
