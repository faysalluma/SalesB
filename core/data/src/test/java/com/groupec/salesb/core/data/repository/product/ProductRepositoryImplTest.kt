package com.groupec.salesb.core.data.repository.product

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toProductList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.CategoryItemResponse
import com.groupec.salesb.core.network.model.ProductItemResponse
import com.groupec.salesb.core.network.model.ProductResponse
import com.groupec.salesb.core.network.model.RayonItemResponse
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

class ProductRepositoryImplTest {

    private val context: Context = mockk(relaxed = true)
    private val apiService: ApiService = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private lateinit var productRepository: ProductRepositoryImpl

    @BeforeEach
    fun setup() {
        every { dataStoreManager.userFlow } returns flowOf(UserStore(id = "12"))
        productRepository = ProductRepositoryImpl(context, apiService, dataStoreManager)
    }

    @Test
    fun `getProducts should return product list when api call is successful`() = runTest {
        // Arrange
        val productResponse = ProductResponse(
            arrayListOf(
                ProductItemResponse(
                    id = 1,
                    reference = "REF-1",
                    libelle = "Café",
                    prixttc = 1200.0,
                    categorie = CategoryItemResponse(id = 3, libelle = "Boissons"),
                    rayon = RayonItemResponse(id = 4, libelle = "A1")
                )
            )
        )
        coEvery { apiService.getProducts("caf", 12) } returns Response.success(productResponse)

        // Act
        val result = productRepository.getProducts("caf")

        // Assert
        assertEquals(Result.Success(productResponse.toProductList()), result)
    }

    @Test
    fun `getProducts should return error when api response is unsuccessful`() = runTest {
        // Arrange
        coEvery { apiService.getProducts("caf", 12) } returns Response.error(
            404,
            "missing".toResponseBody("text/plain".toMediaType())
        )

        // Act
        val result = productRepository.getProducts("caf")

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }

    @Test
    fun `getProducts should return error when api call throws`() = runTest {
        // Arrange
        val exception = Exception("Failed to get products")
        coEvery { apiService.getProducts("caf", 12) } throws exception

        // Act
        val result = productRepository.getProducts("caf")

        // Assert
        assertEquals(Result.Error(exception), result)
    }
}
