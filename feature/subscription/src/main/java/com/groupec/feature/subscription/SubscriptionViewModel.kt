package com.groupec.feature.subscription

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.UpdateUserSubscriptionStatusUseCase
import com.groupec.salesb.core.googlebilling.GoogleBillingEvent
import com.groupec.salesb.core.googlebilling.GoogleBillingProductRequest
import com.groupec.salesb.core.googlebilling.GoogleBillingProductTypes
import com.groupec.salesb.core.googlebilling.GoogleBillingProvider
import com.groupec.salesb.core.model.data.Parameter
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val getParameterUseCase: GetParameterUseCase,
    private val updateUserSubscriptionStatusUseCase: UpdateUserSubscriptionStatusUseCase,
    private val googleBillingProvider: GoogleBillingProvider,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    private val _parameterSate = MutableStateFlow(Parameter())
    val parameterState : StateFlow<Parameter> = _parameterSate.asStateFlow()

    init {
        // The screen reacts to provider state only. The ViewModel keeps UI-specific formatting here.
        observeCatalog()
        observePurchases()
        observeBillingEvents()
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            // We request the Play product catalog every time the screen becomes visible so the CTA
            // reflects the latest product availability and active entitlement state.
            googleBillingProvider.loadCatalog(
                listOf(
                    GoogleBillingProductRequest(
                        productId = SALESB_PRO_MONTHLY_PRODUCT_ID,
                        productType = GoogleBillingProductTypes.SUBS
                    )
                )
            )
        }
    }

    fun startPurchase(activity: Activity) {
        if (_uiState.value.isPurchaseInProgress) return

        viewModelScope.launch {
            _uiState.update { it.copy(isPurchaseInProgress = true, errorMessage = null) }
            val billingResult = googleBillingProvider.launchPurchase(
                activity = activity,
                productId = SALESB_PRO_MONTHLY_PRODUCT_ID
            )

            if (billingResult.responseCode != 0) {
                _uiState.update {
                    it.copy(
                        isPurchaseInProgress = false,
                        pendingMessage = billingResult.debugMessage.toUserBillingMessage(context),
                        pendingMessageIsError = true
                    )
                }
            }
        }
    }

    fun consumeMessage() {
        _uiState.update { it.copy(pendingMessage = null, pendingMessageIsError = false) }
    }

    private fun observeCatalog() {
        viewModelScope.launch {
            googleBillingProvider.catalogState.collect { catalogState ->
                val proProduct = catalogState.products.firstOrNull {
                    it.productId == SALESB_PRO_MONTHLY_PRODUCT_ID
                }

                _uiState.update {
                    it.copy(
                        isLoading = catalogState.isLoading,
                        proPrice = proProduct?.formattedPrice ?: it.proPrice,
                        proPeriodLabel = proProduct?.billingPeriod.toLocalizedPeriod(context),
                        isProPlanAvailable = proProduct != null,
                        errorMessage = catalogState.errorMessage
                            ?.toUserBillingMessage(context)
                            ?.takeIf { proProduct == null }
                    )
                }
            }
        }
    }

    private fun observePurchases() {
        viewModelScope.launch {
            googleBillingProvider.purchaseState.collect { purchaseState ->
                _uiState.update {
                    it.copy(
                        isRefreshing = purchaseState.isRefreshing,
                        isProPlanActive = SALESB_PRO_MONTHLY_PRODUCT_ID in purchaseState.activeProductIds
                    )
                }
            }
        }
    }

    private fun observeBillingEvents() {
        viewModelScope.launch {
            googleBillingProvider.events.collect { event ->
                // Billing SDK callbacks are translated into screen-friendly messages here.
                when (event) {
                    is GoogleBillingEvent.PurchaseCompleted -> {
                        updateUserSubscriptionStatusUseCase(
                            productId = event.productIds.firstOrNull(),
                            purchaseToken = event.purchaseToken
                        )
                        _uiState.update {
                            it.copy(
                                isPurchaseInProgress = false,
                                pendingMessage = context.getString(R.string.subscription_purchase_success),
                                pendingMessageIsError = false
                            )
                        }
                    }

                    is GoogleBillingEvent.PurchasePending -> {
                        _uiState.update {
                            it.copy(
                                isPurchaseInProgress = false,
                                pendingMessage = context.getString(R.string.subscription_purchase_pending),
                                pendingMessageIsError = false
                            )
                        }
                    }

                    GoogleBillingEvent.PurchaseCancelled -> {
                        _uiState.update {
                            it.copy(
                                isPurchaseInProgress = false,
                                pendingMessage = context.getString(R.string.subscription_purchase_cancelled),
                                pendingMessageIsError = true
                            )
                        }
                    }

                    is GoogleBillingEvent.Error -> {
                        _uiState.update {
                            it.copy(
                                isPurchaseInProgress = false,
                                pendingMessage = event.message.toUserBillingMessage(context),
                                pendingMessageIsError = true
                            )
                        }
                    }
                }
            }
        }
    }

    private fun String?.toLocalizedPeriod(context: Context): String {
        return when (this) {
            "P1M" -> context.getString(R.string.subscription_period_month)
            "P1Y" -> context.getString(R.string.subscription_period_year)
            "P1W" -> context.getString(R.string.subscription_period_week)
            else -> context.getString(R.string.subscription_period_default)
        }
    }

    private fun String?.toUserBillingMessage(context: Context): String {
        val message = this?.trim().orEmpty()
        if (message.isBlank()) return context.getString(R.string.subscription_purchase_error)

        return if (message.contains("service disconnected", ignoreCase = true)) {
            context.getString(R.string.subscription_purchase_error)
        } else {
            message
        }
    }

    private companion object {
        /**
         * Must match the subscription id created in Google Play Console.
         */
        const val SALESB_PRO_MONTHLY_PRODUCT_ID = "salesb_pro_monthly"
    }
}

data class SubscriptionUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val isPurchaseInProgress: Boolean = false,
    val isProPlanActive: Boolean = false,
    val isProPlanAvailable: Boolean = false,
    val proPrice: String = "--",
    val proPeriodLabel: String = "",
    val errorMessage: String? = null,
    val pendingMessage: String? = null,
    val pendingMessageIsError: Boolean = false
)
