package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class SubscriptionStatusRequest(
    @SerializedName("productid")
    val billingproductid: String? = null,

    @SerializedName("purchase_token")
    val purchasetoken: String? = null
)
