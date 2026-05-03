package com.groupec.feature.sale

import androidx.paging.PagingData
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.client.GetClientUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.product.GetProductUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.sale.SaveSaleUseCase
import com.groupec.salesb.core.domain.user.GetUserStoreUseCase
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.print.PrintAction
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
class SaleViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val getClientUseCase: GetClientUseCase = mockk()
    private val getProductUseCase: GetProductUseCase = mockk()
    private val getParameterUseCase: GetParameterUseCase = mockk()
    private val getTotalSaleUseCase: GetTotalSaleUseCase = mockk()
    private val getUserStoreUseCase: GetUserStoreUseCase = mockk()
    private val saveSaleUseCase: SaveSaleUseCase = mockk()
    private lateinit var viewModel: SaleViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getUserStoreUseCase() } returns flowOf(UserStore(id = "12"))
        every { getParameterUseCase() } returns flowOf(Parameter(devise = "XOF"))
        every { getClientUseCase() } returns flowOf(emptyList())
        every { getProductUseCase(any()) } returns flowOf(PagingData.from(emptyList<Product>()))
        every { getTotalSaleUseCase(any(), any()) } returns flowOf(0)
        viewModel = SaleViewModel(
            getClientUseCase,
            getProductUseCase,
            getParameterUseCase,
            getTotalSaleUseCase,
            getUserStoreUseCase,
            saveSaleUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getClients should expose id name pairs when clients are available`() = runTest {
        // Arrange
        every { getClientUseCase() } returns flowOf(
            listOf(
                Client(id = 1, nomprenom = "Client A"),
                Client(id = null, nomprenom = "Ignored")
            )
        )

        // Act
        viewModel.getClients()
        advanceUntilIdle()

        // Assert
        assertEquals(listOf("1" to "Client A"), viewModel.clientsUiPairState.value)
    }

    @Test
    fun `addSale should emit success with print action and saved sale when use case succeeds`() = runTest {
        // Arrange
        val sale = Sale(totalprix = 1000.0, details = emptyList())
        val savedSale = sale.copy(id = 5)
        coEvery { saveSaleUseCase(sale) } returns Result.Success(savedSale)

        // Act
        viewModel.addSale(sale, PrintAction.Thermal)
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(PrintAction.Thermal to savedSale), viewModel.addSaleUiState.value)
        coVerify(exactly = 1) { saveSaleUseCase(sale) }
    }

    @Test
    fun `hasReachedFreeMonthlySalesLimit should return true when monthly sales count is above limit`() = runTest {
        // Arrange
        every { getTotalSaleUseCase(any(), any()) } returns flowOf(51)

        // Act
        val result = viewModel.hasReachedFreeMonthlySalesLimit()

        // Assert
        assertTrue(result)
    }
}
