package com.groupec.feature.handleservice

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.handleservice.GetHandleServiceParametersUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActiveClientUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePaymentModeUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateActivePrinterUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateServiceViewUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateShowImageOnProductUseCase
import com.groupec.salesb.core.domain.handleservice.UpdateUseIntForPriceAndAmountUseCase
import com.groupec.salesb.core.model.data.Parameter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HandleServiceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getParametersUseCase: GetHandleServiceParametersUseCase = mockk()
    private val updateServiceViewUseCase: UpdateServiceViewUseCase = mockk()
    private val updateShowImageOnProductUseCase: UpdateShowImageOnProductUseCase = mockk()
    private val updateUseIntForPriceAndAmountUseCase: UpdateUseIntForPriceAndAmountUseCase = mockk()
    private val updateActivePaymentModeUseCase: UpdateActivePaymentModeUseCase = mockk()
    private val updateActiveClientUseCase: UpdateActiveClientUseCase = mockk()
    private val updateActivePrinterUseCase: UpdateActivePrinterUseCase = mockk()
    private lateinit var viewModel: HandleServiceViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getParametersUseCase() } returns flowOf(
            Parameter(
                serviceview = false,
                showimageonproduct = true,
                useintforpriceandamout = false,
                activepaymentmode = true,
                activeClient = true,
                activeprinter = false
            )
        )
        coEvery { updateServiceViewUseCase(any()) } returns Result.Success(Unit)
        coEvery { updateShowImageOnProductUseCase(any()) } returns Result.Success(Unit)
        coEvery { updateUseIntForPriceAndAmountUseCase(any()) } returns Result.Success(Unit)
        coEvery { updateActivePaymentModeUseCase(any()) } returns Result.Success(Unit)
        coEvery { updateActiveClientUseCase(any()) } returns Result.Success(Unit)
        coEvery { updateActivePrinterUseCase(any()) } returns Result.Success(Unit)
        viewModel = HandleServiceViewModel(
            getParametersUseCase,
            updateServiceViewUseCase,
            updateShowImageOnProductUseCase,
            updateUseIntForPriceAndAmountUseCase,
            updateActivePaymentModeUseCase,
            updateActiveClientUseCase,
            updateActivePrinterUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init should expose handle service parameters`() = runTest {
        // Act
        advanceUntilIdle()

        // Assert
        assertEquals(true, viewModel.uiState.value.showImageOnProduct)
        assertEquals(true, viewModel.uiState.value.activePaymentMode)
        assertEquals(false, viewModel.uiState.value.serviceView)
    }

    @Test
    fun `updateServiceView should enable integer price and disable its toggle when service view is active`() = runTest {
        // Arrange
        advanceUntilIdle()

        // Act
        viewModel.updateServiceView(true)
        advanceUntilIdle()

        // Assert
        assertTrue(viewModel.uiState.value.serviceView)
        assertTrue(viewModel.uiState.value.useIntForPriceAndAmount)
        assertTrue(viewModel.uiState.value.isUseIntForPriceAndAmountDisabled)
        coVerify(exactly = 1) { updateServiceViewUseCase(true) }
    }

    @Test
    fun `updateUseIntForPriceAndAmount should not call use case when disabled`() = runTest {
        // Arrange
        advanceUntilIdle()
        viewModel.updateServiceView(true)
        advanceUntilIdle()

        // Act
        viewModel.updateUseIntForPriceAndAmount(false)
        advanceUntilIdle()

        // Assert
        coVerify(exactly = 0) { updateUseIntForPriceAndAmountUseCase(false) }
    }
}
