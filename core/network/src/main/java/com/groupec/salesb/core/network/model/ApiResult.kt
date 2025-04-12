package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class ApiResult<T> (
    @SerializedName("error")
    val error: Boolean = false,

    @SerializedName("data")
    val data: T? = null,

    @SerializedName("message")
    val message: String? = null
)


