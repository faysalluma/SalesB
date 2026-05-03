package com.groupec.feature.rayondetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.rayon.SaveRayonUseCase
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.ui.RayonDataForm
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
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RayonDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveRayonUseCase: SaveRayonUseCase = mockk()
    private lateinit var viewModel: RayonDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RayonDetailViewModel(saveRayonUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addRayon should save mapped rayon and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedRayon = mutableListOf<Rayon>()
        coEvery { saveRayonUseCase(capture(capturedRayon)) } returns Result.Success(Unit)

        // Act
        viewModel.addRayon(RayonDataForm(id = "2", libelle = "A1", description = ""))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addRayonUiState.value)
        assertEquals(2, capturedRayon.single().id)
        assertEquals("A1", capturedRayon.single().libelle)
        assertNull(capturedRayon.single().description)
        coVerify(exactly = 1) { saveRayonUseCase(any()) }
    }

    @Test
    fun `addRayon should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { saveRayonUseCase(any()) } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.addRayon(RayonDataForm(libelle = "A1"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.addRayonUiState.value)
    }
}
