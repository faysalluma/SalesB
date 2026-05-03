package com.groupec.feature.outputdetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.SaveOutputUseCase
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.ui.OutputDataForm
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
class OutputDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveOutputUseCase: SaveOutputUseCase = mockk()
    private lateinit var viewModel: OutPutDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = OutPutDetailViewModel(saveOutputUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addOutput should save mapped output and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedOutput = mutableListOf<Output>()
        coEvery { saveOutputUseCase(capture(capturedOutput)) } returns Result.Success(Unit)

        // Act
        viewModel.addOutput(OutputDataForm(id = "9", description = "Transport", prix = "1500.5"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addOutputUiState.value)
        assertEquals(9, capturedOutput.single().id)
        assertEquals("Transport", capturedOutput.single().description)
        assertEquals(1500.5, capturedOutput.single().prix)
        coVerify(exactly = 1) { saveOutputUseCase(any()) }
    }

    @Test
    fun `addOutput should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { saveOutputUseCase(any()) } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.addOutput(OutputDataForm(description = "Transport", prix = "1500"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.addOutputUiState.value)
    }
}
