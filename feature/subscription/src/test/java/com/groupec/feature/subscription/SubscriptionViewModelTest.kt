package com.groupec.feature.subscription

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.UpdateUserSubscriptionStatusUseCase
import com.groupec.salesb.core.googlebilling.GoogleBillingCatalogState
import com.groupec.salesb.core.googlebilling.GoogleBillingEvent
import com.groupec.salesb.core.googlebilling.GoogleBillingProduct
import com.groupec.salesb.core.googlebilling.GoogleBillingProductRequest
import com.groupec.salesb.core.googlebilling.GoogleBillingProductTypes
import com.groupec.salesb.core.googlebilling.GoogleBillingProvider
import com.groupec.salesb.core.googlebilling.GoogleBillingPurchaseState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SubscriptionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getParameterUseCase: GetParameterUseCase = mockk()
    private val updateUserSubscriptionStatusUseCase: UpdateUserSubscriptionStatusUseCase = mockk()
    private val googleBillingProvider: GoogleBillingProvider = mockk()
    private val context: Context = mockk()
    private val catalogState = MutableStateFlow(GoogleBillingCatalogState())
    private val purchaseState = MutableStateFlow(GoogleBillingPurchaseState())
    private val events = MutableSharedFlow<GoogleBillingEvent>()
    private lateinit var viewModel: SubscriptionViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getParameterUseCase() } returns flowOf()
        every { googleBillingProvider.catalogState } returns catalogState
        every { googleBillingProvider.purchaseState } returns purchaseState
        every { googleBillingProvider.events } returns events
        every { context.getString(R.string.subscription_period_month) } returns "month"
        every { context.getString(R.string.subscription_period_year) } returns "year"
        every { context.getString(R.string.subscription_period_week) } returns "week"
        every { context.getString(R.string.subscription_period_default) } returns "period"
        every { context.getString(R.string.subscription_purchase_success) } returns "success"
        every { context.getString(R.string.subscription_purchase_pending) } returns "pending"
        every { context.getString(R.string.subscription_purchase_cancelled) } returns "cancelled"
        every { context.getString(R.string.subscription_purchase_error) } returns "purchase error"
        coEvery { googleBillingProvider.loadCatalog(any()) } returns Unit
        coEvery { updateUserSubscriptionStatusUseCase(any(), any()) } returns Result.Success(Unit)
        viewModel = SubscriptionViewModel(
            getParameterUseCase,
            updateUserSubscriptionStatusUseCase,
            googleBillingProvider,
            context
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `refresh should request monthly pro subscription catalog`() = runTest {
        // Act
        advanceUntilIdle()

        // Assert
        coVerify(atLeast = 1) {
            googleBillingProvider.loadCatalog(
                listOf(
                    GoogleBillingProductRequest(
                        productId = "salesb_pro_monthly",
                        productType = GoogleBillingProductTypes.SUBS
                    )
                )
            )
        }
    }

    @Test
    fun `catalog state should expose pro price and monthly period when product is available`() = runTest {
        // Act
        catalogState.value = GoogleBillingCatalogState(
            products = listOf(
                GoogleBillingProduct(
                    productId = "salesb_pro_monthly",
                    productType = GoogleBillingProductTypes.SUBS,
                    title = "SalesB Pro",
                    description = "Monthly plan",
                    formattedPrice = "4.99 EUR",
                    billingPeriod = "P1M",
                    offerToken = "token"
                )
            )
        )
        advanceUntilIdle()

        // Assert
        assertEquals("4.99 EUR", viewModel.uiState.value.proPrice)
        assertEquals("month", viewModel.uiState.value.proPeriodLabel)
        assertTrue(viewModel.uiState.value.isProPlanAvailable)
    }

    @Test
    fun `purchase completed event should update subscription status and emit success message`() = runTest {
        // Act
        events.emit(
            GoogleBillingEvent.PurchaseCompleted(
                productIds = listOf("salesb_pro_monthly"),
                purchaseToken = "purchase-token"
            )
        )
        advanceUntilIdle()

        // Assert
        assertFalse(viewModel.uiState.value.isPurchaseInProgress)
        assertEquals("success", viewModel.uiState.value.pendingMessage)
        assertFalse(viewModel.uiState.value.pendingMessageIsError)
        coVerify(exactly = 1) {
            updateUserSubscriptionStatusUseCase(
                productId = "salesb_pro_monthly",
                purchaseToken = "purchase-token"
            )
        }
    }
}
