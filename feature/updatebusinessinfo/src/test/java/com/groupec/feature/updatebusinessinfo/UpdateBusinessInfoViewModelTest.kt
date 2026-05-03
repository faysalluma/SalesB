package com.groupec.feature.updatebusinessinfo

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.parameter.UpdateBusinessInfoUseCase
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateBusinessInfoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getParameterUseCase: GetParameterUseCase = mockk()
    private val updateBusinessInfoUseCase: UpdateBusinessInfoUseCase = mockk()
    private lateinit var viewModel: UpdateBusinessInfoViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateBusinessInfo should trim and normalize form before saving`() = runTest {
        // Arrange
        val current = Parameter(id = 1, raisonsociale = "Old", devise = "xof", tva = 10.0)
        val updated = current.copy(raisonsociale = "New company", devise = "EUR", tva = 18.0)
        val capturedParameter = mutableListOf<Parameter>()
        val capturedTva = mutableListOf<String?>()
        every { getParameterUseCase() } returns flowOf(current)
        coEvery {
            updateBusinessInfoUseCase(
                capture(capturedParameter),
                null,
                captureNullable(capturedTva)
            )
        } returns Result.Success(updated)
        viewModel = UpdateBusinessInfoViewModel(getParameterUseCase, updateBusinessInfoUseCase)
        advanceUntilIdle()

        // Act
        viewModel.updateBusinessInfo(
            UpdateBusinessFormData(
                companyName = "  New company  ",
                companyType = 2,
                email = " contact@salesb.test ",
                address = " ",
                phone = " 123 ",
                ifu = " IFU ",
                website = "",
                devise = " eur ",
                tva = "18"
            ),
            uri = null
        )
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.updateBusinessUiState.value)
        assertEquals(updated, viewModel.parameterState.value)
        assertEquals("New company", capturedParameter.single().raisonsociale)
        assertEquals(2, capturedParameter.single().entreprisetype)
        assertEquals("IFU", capturedParameter.single().ifu)
        assertNull(capturedParameter.single().adresse)
        assertEquals("123", capturedParameter.single().telephone)
        assertEquals("contact@salesb.test", capturedParameter.single().email)
        assertNull(capturedParameter.single().website)
        assertEquals("EUR", capturedParameter.single().devise)
        assertEquals(18.0, capturedParameter.single().tva)
        assertEquals("18", capturedTva.single())
        coVerify(exactly = 1) { updateBusinessInfoUseCase(any(), null, any()) }
    }

    @Test
    fun `updateBusinessInfo should keep existing tva and emit error when use case fails`() = runTest {
        // Arrange
        val current = Parameter(id = 1, raisonsociale = "Old", devise = "XOF", tva = 10.0)
        val capturedParameter = mutableListOf<Parameter>()
        every { getParameterUseCase() } returns flowOf(current)
        coEvery {
            updateBusinessInfoUseCase(capture(capturedParameter), null, "abc")
        } returns Result.Error(IllegalStateException("save failed"))
        viewModel = UpdateBusinessInfoViewModel(getParameterUseCase, updateBusinessInfoUseCase)
        advanceUntilIdle()

        // Act
        viewModel.updateBusinessInfo(UpdateBusinessFormData(companyName = "Name", devise = "xof", tva = "abc"), null)
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.updateBusinessUiState.value)
        assertEquals(10.0, capturedParameter.single().tva)
    }
}
