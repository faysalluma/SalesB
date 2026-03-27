package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class SubscriptionStatusRequest(
    @SerializedName("productId")
    val productId: String? = null,

    @SerializedName("purchaseToken")
    val purchaseToken: String? = null
)