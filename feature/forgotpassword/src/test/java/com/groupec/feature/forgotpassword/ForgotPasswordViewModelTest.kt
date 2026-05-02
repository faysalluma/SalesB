package com.groupec.feature.forgotpassword

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.user.ForgotPasswordUseCase
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
class ForgotPasswordViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val forgotPasswordUseCase: ForgotPasswordUseCase = mockk()
    private lateinit var viewModel: ForgotPasswordViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ForgotPasswordViewModel(forgotPasswordUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `resetPassword should emit success and pass email when use case succeeds`() = runTest {
        // Arrange
        coEvery { forgotPasswordUseCase("user@salesb.test") } returns Result.Success(Unit)

        // Act
        viewModel.resetPassword("user@salesb.test")
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.forgotPasswordUiState.value)
        coVerify(exactly = 1) { forgotPasswordUseCase("user@salesb.test") }
    }

    @Test
    fun `resetPassword should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { forgotPasswordUseCase(any()) } returns Result.Error(IllegalStateException("unknown email"))

        // Act
        viewModel.resetPassword("user@salesb.test")
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("unknown email"), viewModel.forgotPasswordUiState.value)
    }
}
