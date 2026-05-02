package com.groupec.feature.productdetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.category.GetCategoryUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.product.GetAllProductsUseCase
import com.groupec.salesb.core.domain.product.SaveProductUseCase
import com.groupec.salesb.core.domain.rayon.GetRayonUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.ui.ProductDataForm
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.emptyFlow
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
class ProductDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveProductUseCase: SaveProductUseCase = mockk()
    private val getAllProductsUseCase: GetAllProductsUseCase = mockk()
    private val getCategoryUseCase: GetCategoryUseCase = mockk()
    private val getRayonUseCase: GetRayonUseCase = mockk()
    private val getParameterUseCase: GetParameterUseCase = mockk()
    private val getUserStoreUseCase: GetUserStoreUseCase = mockk()
    private lateinit var viewModel: ProductDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getUserStoreUseCase() } returns flowOf(UserStore(id = "12"))
        every { getParameterUseCase() } returns flowOf(Parameter(devise = "XOF"))
        every { getCategoryUseCase() } returns emptyFlow()
        every { getRayonUseCase(any()) } returns emptyFlow()
        coEvery { getAllProductsUseCase("") } returns Result.Success(emptyList())
        viewModel = ProductDetailViewModel(
            saveProductUseCase,
            getAllProductsUseCase,
            getCategoryUseCase,
            getRayonUseCase,
            getParameterUseCase,
            getUserStoreUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addProduct should save mapped product and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedProduct = mutableListOf<Product>()
        coEvery { saveProductUseCase(capture(capturedProduct), null) } returns Result.Success(Unit)
        advanceUntilIdle()

        // Act
        viewModel.addProduct(
            ProductDataForm(
                id = "9",
                reference = "",
                libelle = "Café",
                description = "",
                prixttc = "1200.5",
                qtestock = "10",
                stockmini = "",
                categorieid = "3",
                rayonid = "4",
                image = ""
            ),
            uri = null
        )
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addProductUiState.value)
        assertEquals(9, capturedProduct.single().id)
        assertNull(capturedProduct.single().reference)
        assertEquals("Café", capturedProduct.single().libelle)
        assertEquals(1200.5, capturedProduct.single().prixttc)
        assertEquals(10, capturedProduct.single().qtestock)
        assertNull(capturedProduct.single().stockmini)
        assertEquals(3, capturedProduct.single().categorieid)
        assertEquals(4, capturedProduct.single().rayonid)
        coVerify(exactly = 1) { saveProductUseCase(any(), null) }
    }

    @Test
    fun `refreshTotalProductsCount should expose product count when use case succeeds`() = runTest {
        // Arrange
        coEvery { getAllProductsUseCase("") } returns Result.Success(
            listOf(
                Product(libelle = "A", prixttc = 1.0),
                Product(libelle = "B", prixttc = 2.0)
            )
        )

        // Act
        viewModel.refreshTotalProductsCount()
        advanceUntilIdle()

        // Assert
        assertEquals(2, viewModel.totalProductsCountState.value)
    }
}
