package com.groupec.feature.categorydetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.SaveCategoryUseCase
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.ui.CategoryDataForm
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
class CategoryDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveCategoryUseCase: SaveCategoryUseCase = mockk()
    private lateinit var viewModel: CategoryDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CategoryDetailViewModel(saveCategoryUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addCategory should save mapped category and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedCategory = mutableListOf<Category>()
        coEvery { saveCategoryUseCase(capture(capturedCategory)) } returns Result.Success(Unit)

        // Act
        viewModel.addCategory(CategoryDataForm(id = "7", libelle = "Boissons", description = ""))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addCategoryUiState.value)
        assertEquals(7, capturedCategory.single().id)
        assertEquals("Boissons", capturedCategory.single().libelle)
        assertNull(capturedCategory.single().description)
        coVerify(exactly = 1) { saveCategoryUseCase(any()) }
    }

    @Test
    fun `addCategory should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { saveCategoryUseCase(any()) } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.addCategory(CategoryDataForm(libelle = "Boissons"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.addCategoryUiState.value)
    }

    @Test
    fun `resetFlow should emit idle state`() {
        // Act
        viewModel.resetFlow()

        // Assert
        assertEquals(FormUIState.Idle, viewModel.addCategoryUiState.value)
    }
}
