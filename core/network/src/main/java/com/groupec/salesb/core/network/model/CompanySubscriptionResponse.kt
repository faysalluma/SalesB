package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class CompanySubscriptionResponse(
    @SerializedName("subscription") val subscription: CompanySubscriptionItemResponse
)

data class CompanySubscriptionItemResponse(
    @SerializedName("is_entitled") val isEntitled: Int
)
