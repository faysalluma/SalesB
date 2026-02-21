package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class ParameterResponse(
    @SerializedName("parameter")
    val parameter : ParamItemResponse
)
data class ParamItemResponse(
    @SerializedName("devise")
    val devise: String,

    @SerializedName("raisonsociale")
    val raisonsociale: String,

    @SerializedName("adresse")
    val adresse: String,

    @SerializedName("telephone")
    val telephone: String,

    @SerializedName("email")
    val email: String?,

    @SerializedName("ifu")
    val ifu: String?,

    @SerializedName("website")
    val website: String?,

    @SerializedName("expirationdate")
    val expirationdate: String ? = null,

    @SerializedName("typeentreprise")
    val typeentreprise: String,

    @SerializedName("offline")
    val offline: Int,

    @SerializedName("primarycolor")
    val primarycolor: String,

    @SerializedName("secondarycolor")
    val secondarycolor: String,

    @SerializedName("showimageonproduct")
    val showimageonproduct: Int,

    @SerializedName("defaultpaymenttype")
    val defaultpaymenttype: String ? = null,

    @SerializedName("tva")
    val tva: Double
)