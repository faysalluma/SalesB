package com.groupec.salesb.core.googlebilling

/**
 * Billing requests are intentionally plain Kotlin models so feature modules can consume
 * the billing layer without depending on Play Billing SDK types directly.
 */
data class GoogleBillingProductRequest(
    val productId: String,
    val productType: String
)

object GoogleBillingProductTypes {
    const val SUBS = "subs"
    const val INAPP = "inapp"
}

data class GoogleBillingProduct(
    val productId: String,
    val productType: String,
    val title: String,
    val description: String,
    val formattedPrice: String,
    val billingPeriod: String?,
    val offerToken: String?
)

data class GoogleBillingPurchase(
    val orderId: String?,
    val purchaseToken: String,
    val productIds: List<String>,
    val productType: String,
    val isAcknowledged: Boolean,
    val isAutoRenewing: Boolean,
    val isPending: Boolean
)

data class GoogleBillingCatalogState(
    val isLoading: Boolean = false,
    val products: List<GoogleBillingProduct> = emptyList(),
    val errorMessage: String? = null
)

data class GoogleBillingPurchaseState(
    val isRefreshing: Boolean = false,
    val purchases: List<GoogleBillingPurchase> = emptyList(),
    val activeProductIds: Set<String> = emptySet()
)

data class GoogleBillingOperationResult(
    val responseCode: Int,
    val debugMessage: String
)

sealed interface GoogleBillingEvent {
    data class PurchaseCompleted(
        val productIds: List<String>,
        val purchaseToken: String
    ) : GoogleBillingEvent
    data class PurchasePending(val productIds: List<String>) : GoogleBillingEvent
    data object PurchaseCancelled : GoogleBillingEvent
    data class Error(val message: String) : GoogleBillingEvent
}
