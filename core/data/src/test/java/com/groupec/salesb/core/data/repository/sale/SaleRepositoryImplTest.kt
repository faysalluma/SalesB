package com.groupec.salesb.core.data.repository.sale

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toSaleList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.ClientReducedResponse
import com.groupec.salesb.core.network.model.ProductReducedResponse
import com.groupec.salesb.core.network.model.SaleItemResponse
import com.groupec.salesb.core.network.model.SaleResponse
import com.groupec.salesb.core.network.model.UserReducedResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response

class SaleRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private val context: Context = mockk(relaxed = true)
    private lateinit var saleRepository: SaleRepositoryImpl

    @BeforeEach
    fun setup() {
        every { dataStoreManager.userFlow } returns flowOf(UserStore(id = "12"))
        saleRepository = SaleRepositoryImpl(apiService, dataStoreManager, context)
    }

    @Test
    fun `getAllSales should return sales when api call is successful`() = runTest {
        // Arrange
        val response = SaleResponse(arrayListOf(saleResponseItem()))
        val searchParams = mapOf("search" to "caf")
        coEvery { apiService.getSales(searchParams, 12) } returns Response.success(response)

        // Act
        val result = saleRepository.getAllSales(searchParams)

        // Assert
        assertEquals(Result.Success(response.toSaleList()), result)
    }

    @Test
    fun `getAllSales should return error when api response is unsuccessful`() = runTest {
        // Arrange
        val searchParams = mapOf("search" to "caf")
        coEvery { apiService.getSales(searchParams, 12) } returns Response.error(
            500,
            "server error".toResponseBody("text/plain".toMediaType())
        )

        // Act
        val result = saleRepository.getAllSales(searchParams)

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }

    @Test
    fun `saveSale should send current user id with sale`() = runTest {
        // Arrange
        val capturedSale = mutableListOf<Sale>()
        val sale = Sale(totalprix = 2500.0, details = emptyList())
        coEvery { apiService.addSale(capture(capturedSale)) } returns Response.success(
            com.groupec.salesb.core.network.model.ApiResult(data = saleResponseItem())
        )

        // Act
        val result = saleRepository.saveSale(sale)

        // Assert
        assertTrue(result is Result.Success)
        assertEquals(12, capturedSale.single().userid)
    }

    private fun saleResponseItem(): SaleItemResponse {
        return SaleItemResponse(
            id = 1,
            datevente = "2026-05-01 10:00:00",
            totalprix = 2500.0,
            client = ClientReducedResponse(id = 2, nomprenom = "Client"),
            paymenttype = "cash",
            user = UserReducedResponse(id = 12, nomprenom = "Admin"),
            products = arrayListOf(ProductReducedResponse(id = 3, libelle = "Café", qte = 2.0, prix = 1250.0))
        )
    }
}
