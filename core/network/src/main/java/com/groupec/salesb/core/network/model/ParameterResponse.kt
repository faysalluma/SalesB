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

    @SerializedName("typeentreprise")
    val typeentreprise: String,

    @SerializedName("mode")
    val mode: String,

    @SerializedName("primarycolor")
    val primarycolor: String,

    @SerializedName("secondarycolor")
    val secondarycolor: String,

    @SerializedName("loadproducts")
    val loadproducts: Int
)