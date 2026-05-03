package com.groupec.feature.termsandconditions

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.AcceptTermsAndConditionsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TermsAndConditionsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val acceptTermsAndConditionsUseCase: AcceptTermsAndConditionsUseCase = mockk()
    private lateinit var viewModel: TermsAndConditionsViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TermsAndConditionsViewModel(acceptTermsAndConditionsUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `acceptTermsAndConditions should emit success when use case succeeds`() = runTest {
        // Arrange
        coEvery { acceptTermsAndConditionsUseCase() } returns Result.Success(Unit)

        // Act
        viewModel.acceptTermsAndConditions()
        advanceUntilIdle()

        // Assert
        assertEquals(TermsAndConditionsUiState.Success, viewModel.termsAndConditionsUiState.value)
        coVerify(exactly = 1) { acceptTermsAndConditionsUseCase() }
    }

    @Test
    fun `acceptTermsAndConditions should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { acceptTermsAndConditionsUseCase() } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.acceptTermsAndConditions()
        advanceUntilIdle()

        // Assert
        assertEquals(TermsAndConditionsUiState.Error("save failed"), viewModel.termsAndConditionsUiState.value)
    }
}
