package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class SubscriptionStatusRequest(
    @SerializedName("billingproductid")
    val billingproductid: String? = null,

    @SerializedName("purchasetoken")
    val purchasetoken: String? = null
)