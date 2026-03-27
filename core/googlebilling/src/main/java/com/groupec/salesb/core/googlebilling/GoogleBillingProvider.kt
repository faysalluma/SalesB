package com.groupec.salesb.core.googlebilling

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Central Play Billing entry point for the app.
 *
 * This class owns a single BillingClient connection and exposes framework-agnostic state to the
 * rest of the project. Feature modules only see plain Kotlin models and do not deal with Billing SDK
 * callback types directly.
 *
 * Important production note:
 * Google recommends verifying subscription purchases on a secure backend before granting durable
 * entitlements. This provider currently acknowledges purchases client-side so the in-app flow works
 * end-to-end, but backend verification should be added before using billing as a hard security gate.
 */
@Singleton
class GoogleBillingProvider @Inject constructor(
    @ApplicationContext context: Context
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val _catalogState = MutableStateFlow(GoogleBillingCatalogState())
    val catalogState: StateFlow<GoogleBillingCatalogState> = _catalogState.asStateFlow()

    private val _purchaseState = MutableStateFlow(GoogleBillingPurchaseState())
    val purchaseState: StateFlow<GoogleBillingPurchaseState> = _purchaseState.asStateFlow()

    private val _events = MutableSharedFlow<GoogleBillingEvent>(extraBufferCapacity = 4)
    val events: SharedFlow<GoogleBillingEvent> = _events.asSharedFlow()

    private var trackedRequests: List<GoogleBillingProductRequest> = emptyList()
    private val cachedProductDetails = mutableMapOf<String, ResolvedBillingProduct>()

    private val purchasesUpdatedListener = PurchasesUpdatedListener { billingResult, purchases ->
        when {
            billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null -> {
                scope.launch {
                    val productType = trackedRequests.firstOrNull()?.productType ?: BillingClient.ProductType.SUBS
                    handlePurchases(
                        purchases = purchases,
                        productType = productType,
                        emitCompletionEvents = true
                    )
                    refreshPurchases()
                }
            }

            billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED -> {
                _events.tryEmit(GoogleBillingEvent.PurchaseCancelled)
            }

            else -> {
                _events.tryEmit(
                    GoogleBillingEvent.Error(
                        billingResult.debugMessage.ifBlank { "The Google Play purchase failed." }
                    )
                )
            }
        }
    }

    private val billingClient: BillingClient = BillingClient
        .newBuilder(context)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases(
            PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    suspend fun loadCatalog(requests: List<GoogleBillingProductRequest>) {
        if (requests.isEmpty()) {
            _catalogState.value = GoogleBillingCatalogState()
            return
        }

        trackedRequests = requests
        _catalogState.update { it.copy(isLoading = true, errorMessage = null) }

        val featureSupported = billingClient.isFeatureSupported(BillingClient.FeatureType.PRODUCT_DETAILS)
        if (featureSupported.responseCode != BillingClient.BillingResponseCode.OK) {
            _catalogState.value = GoogleBillingCatalogState(
                errorMessage = featureSupported.debugMessage.ifBlank {
                    "Product details are not supported on this device."
                }
            )
            return
        }

        val connectionResult = connectIfNeeded()
        if (connectionResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _catalogState.value = GoogleBillingCatalogState(
                errorMessage = connectionResult.debugMessage.ifBlank { "Unable to connect to Google Play." }
            )
            return
        }

        val queryResult = queryProductDetails(requests)
        if (queryResult.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _catalogState.value = GoogleBillingCatalogState(
                errorMessage = queryResult.billingResult.debugMessage.ifBlank {
                    "Unable to load Google Play products."
                }
            )
            return
        }

        cachedProductDetails.clear()
        val products = queryResult.products.mapNotNull { productDetails ->
            productDetails.toGoogleBillingProduct()?.also { resolved ->
                cachedProductDetails[resolved.product.productId] = resolved
            }?.product
        }

        val errorMessage = if (products.isEmpty()) {
            "No Google Play products were returned. Check the product ids and Play Console setup."
        } else {
            null
        }

        _catalogState.value = GoogleBillingCatalogState(
            isLoading = false,
            products = products,
            errorMessage = errorMessage
        )

        refreshPurchases()
    }

    suspend fun refreshPurchases() {
        val trackedProductTypes = trackedRequests.map { it.productType }.toSet()
        if (trackedProductTypes.isEmpty()) {
            _purchaseState.value = GoogleBillingPurchaseState()
            return
        }

        val connectionResult = connectIfNeeded()
        if (connectionResult.responseCode != BillingClient.BillingResponseCode.OK) {
            _events.tryEmit(
                GoogleBillingEvent.Error(
                    connectionResult.debugMessage.ifBlank { "Unable to connect to Google Play." }
                )
            )
            return
        }

        _purchaseState.update { it.copy(isRefreshing = true) }

        val allPurchases = mutableListOf<GoogleBillingPurchase>()
        trackedProductTypes.forEach { productType ->
            val purchaseResult = queryPurchases(productType)
            if (purchaseResult.billingResult.responseCode != BillingClient.BillingResponseCode.OK) {
                _events.tryEmit(
                    GoogleBillingEvent.Error(
                        purchaseResult.billingResult.debugMessage.ifBlank {
                            "Unable to refresh Google Play purchases."
                        }
                    )
                )
                return@forEach
            }

            handlePurchases(
                purchases = purchaseResult.purchases,
                productType = productType,
                emitCompletionEvents = false
            )

            allPurchases += purchaseResult.purchases.map { purchase ->
                purchase.toGoogleBillingPurchase(productType = productType)
            }
        }

        _purchaseState.value = GoogleBillingPurchaseState(
            isRefreshing = false,
            purchases = allPurchases,
            activeProductIds = allPurchases
                .filterNot { it.isPending }
                .flatMap { it.productIds }
                .toSet()
        )
    }

    /**
     * Launches the Google Play purchase sheet for a product previously returned by [loadCatalog].
     */
    suspend fun launchPurchase(
        activity: Activity,
        productId: String
    ): GoogleBillingOperationResult {
        val connectionResult = connectIfNeeded()
        if (connectionResult.responseCode != BillingClient.BillingResponseCode.OK) {
            return connectionResult.toOperationResult()
        }

        val resolvedProduct = cachedProductDetails[productId]
            ?: return GoogleBillingOperationResult(
                responseCode = BillingClient.BillingResponseCode.ITEM_UNAVAILABLE,
                debugMessage = "The selected Google Play product is not loaded."
            )

        val productParamsBuilder = BillingFlowParams.ProductDetailsParams
            .newBuilder()
            .setProductDetails(resolvedProduct.productDetails)

        // Subscriptions require an offer token. We keep the first eligible offer returned by Play.
        resolvedProduct.offerToken?.let(productParamsBuilder::setOfferToken)

        val billingFlowParams = BillingFlowParams
            .newBuilder()
            .setProductDetailsParamsList(listOf(productParamsBuilder.build()))
            .build()

        return billingClient.launchBillingFlow(activity, billingFlowParams).toOperationResult()
    }

    private suspend fun connectIfNeeded(): BillingResult {
        if (billingClient.isReady) {
            return BillingResult.newBuilder()
                .setResponseCode(BillingClient.BillingResponseCode.OK)
                .setDebugMessage("Already connected.")
                .build()
        }

        return suspendCancellableCoroutine { continuation ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingServiceDisconnected() {
                    if (continuation.isActive) {
                        continuation.resume(
                            BillingResult.newBuilder()
                                .setResponseCode(BillingClient.BillingResponseCode.SERVICE_DISCONNECTED)
                                .setDebugMessage("Google Play Billing service disconnected.")
                                .build()
                        )
                    }
                }

                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (continuation.isActive) {
                        continuation.resume(billingResult)
                    }
                }
            })
        }
    }

    private suspend fun queryProductDetails(
        requests: List<GoogleBillingProductRequest>
    ): QueryProductResult = suspendCancellableCoroutine { continuation ->
        val queryProducts = requests.map {
            QueryProductDetailsParams.Product
                .newBuilder()
                .setProductId(it.productId)
                .setProductType(it.productType)
                .build()
        }

        val params = QueryProductDetailsParams
            .newBuilder()
            .setProductList(queryProducts)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, productDetailsList ->
            if (continuation.isActive) {
                continuation.resume(
                    QueryProductResult(
                        billingResult = billingResult,
                        products = productDetailsList
                    )
                )
            }
        }
    }

    private suspend fun queryPurchases(
        productType: String
    ): QueryPurchasesResult = suspendCancellableCoroutine { continuation ->
        val params = QueryPurchasesParams
            .newBuilder()
            .setProductType(productType)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (continuation.isActive) {
                continuation.resume(
                    QueryPurchasesResult(
                        billingResult = billingResult,
                        purchases = purchases
                    )
                )
            }
        }
    }

    private suspend fun acknowledgePurchase(purchaseToken: String): BillingResult =
        suspendCancellableCoroutine { continuation ->
            val params = AcknowledgePurchaseParams
                .newBuilder()
                .setPurchaseToken(purchaseToken)
                .build()

            billingClient.acknowledgePurchase(params) { billingResult ->
                if (continuation.isActive) {
                    continuation.resume(billingResult)
                }
            }
        }

    private suspend fun handlePurchases(
        purchases: List<Purchase>,
        productType: String,
        emitCompletionEvents: Boolean
    ) {
        purchases.forEach { purchase ->
            when (purchase.purchaseState) {
                Purchase.PurchaseState.PURCHASED -> {
                    if (!purchase.isAcknowledged) {
                        val acknowledgeResult = acknowledgePurchase(purchase.purchaseToken)
                        if (acknowledgeResult.responseCode != BillingClient.BillingResponseCode.OK) {
                            _events.tryEmit(
                                GoogleBillingEvent.Error(
                                    acknowledgeResult.debugMessage.ifBlank {
                                        "Unable to acknowledge the Google Play purchase."
                                    }
                                )
                            )
                            return@forEach
                        }
                    }

                    if (emitCompletionEvents) {
                        _events.tryEmit(
                            GoogleBillingEvent.PurchaseCompleted(
                                productIds = purchase.products,
                                purchaseToken = purchase.purchaseToken
                            )
                        )
                    }
                }

                Purchase.PurchaseState.PENDING -> {
                    if (emitCompletionEvents) {
                        _events.tryEmit(GoogleBillingEvent.PurchasePending(purchase.products))
                    }
                }
            }
        }

        _purchaseState.update {
            val mappedPurchases = purchases.map { purchase ->
                purchase.toGoogleBillingPurchase(productType = productType)
            }
            val mergedPurchases = (it.purchases + mappedPurchases)
                .associateBy { purchase -> purchase.purchaseToken }
                .values
                .toList()

            it.copy(
                isRefreshing = false,
                purchases = mergedPurchases,
                activeProductIds = mergedPurchases
                    .filterNot { purchase -> purchase.isPending }
                    .flatMap { purchase -> purchase.productIds }
                    .toSet()
            )
        }
    }

    private fun ProductDetails.toGoogleBillingProduct(): ResolvedBillingProduct? {
        val selectedOffer = when (productType) {
            BillingClient.ProductType.SUBS -> {
                subscriptionOfferDetails
                    ?.minByOrNull { offer ->
                        offer.pricingPhases.pricingPhaseList
                            .lastOrNull()
                            ?.priceAmountMicros ?: Long.MAX_VALUE
                    }
            }

            else -> null
        }

        val pricingPhase = selectedOffer
            ?.pricingPhases
            ?.pricingPhaseList
            ?.lastOrNull()

        val formattedPrice = pricingPhase?.formattedPrice
            ?: oneTimePurchaseOfferDetails?.formattedPrice
            ?: ""

        val billingPeriod = pricingPhase?.billingPeriod

        val product = GoogleBillingProduct(
            productId = productId,
            productType = productType,
            title = title,
            description = description,
            formattedPrice = formattedPrice,
            billingPeriod = billingPeriod,
            offerToken = selectedOffer?.offerToken
        )

        return ResolvedBillingProduct(
            product = product,
            productDetails = this,
            offerToken = selectedOffer?.offerToken
        )
    }

    private fun Purchase.toGoogleBillingPurchase(productType: String): GoogleBillingPurchase {
        return GoogleBillingPurchase(
            orderId = orderId,
            purchaseToken = purchaseToken,
            productIds = products,
            productType = productType,
            isAcknowledged = isAcknowledged,
            isAutoRenewing = isAutoRenewing,
            isPending = purchaseState == Purchase.PurchaseState.PENDING
        )
    }

    private data class QueryProductResult(
        val billingResult: BillingResult,
        val products: List<ProductDetails>
    )

    private data class QueryPurchasesResult(
        val billingResult: BillingResult,
        val purchases: List<Purchase>
    )

    private data class ResolvedBillingProduct(
        val product: GoogleBillingProduct,
        val productDetails: ProductDetails,
        val offerToken: String?
    )

    private fun BillingResult.toOperationResult(): GoogleBillingOperationResult {
        return GoogleBillingOperationResult(
            responseCode = responseCode,
            debugMessage = debugMessage
        )
    }
}
