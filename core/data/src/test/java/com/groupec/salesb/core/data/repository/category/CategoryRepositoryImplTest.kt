package com.groupec.salesb.core.data.repository.category

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toCategorieList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.model.CategoryItemResponse
import com.groupec.salesb.core.network.model.CategoryResponse
import com.groupec.salesb.core.network.model.UserReducedResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.HttpException
import retrofit2.Response

class CategoryRepositoryImplTest {

    private val apiService: ApiService = mockk()
    private val dataStoreManager: DataStoreManager = mockk()
    private lateinit var categoryRepository: CategoryRepositoryImpl

    @BeforeEach
    fun setup() {
        every { dataStoreManager.userFlow } returns flowOf(UserStore(id = "12"))
        categoryRepository = CategoryRepositoryImpl(apiService, dataStoreManager)
    }

    @Test
    fun `getCategories should return list of categories when api call is successful`() = runTest {
        // Arrange
        val categoryResponse = CategoryResponse(
            arrayListOf(
                CategoryItemResponse(
                    id = 1,
                    libelle = "Boissons",
                    description = "Frais",
                    user = UserReducedResponse(id = 12, nomprenom = "Admin")
                ),
                CategoryItemResponse(id = 2, libelle = "Epicerie")
            )
        )
        coEvery { apiService.getCategories(12) } returns Response.success(categoryResponse)

        // Act
        val result = categoryRepository.getCategories().first()

        // Assert
        assertEquals(categoryResponse.toCategorieList(), result)
        coVerify(exactly = 1) { apiService.getCategories(12) }
    }

    @Test
    fun `getCategories should throw an exception when API call fails`() = runTest {
        // Arrange
        val exception = Exception("Failed to get categories")
        coEvery { apiService.getCategories(12) } throws exception

        // Act
        val result = categoryRepository.getCategories()

        // Assert
        assertThrows<Exception> {
            result.first()
        }.also { thrown ->
            assertEquals("Failed to get categories", thrown.message)
        }
    }

    @Test
    fun `getAllCategories should return success when api call is successful`() = runTest {
        // Arrange
        val categoryResponse = CategoryResponse(arrayListOf(CategoryItemResponse(id = 1, libelle = "Boissons")))
        coEvery { apiService.getAllCategories("boi", 12) } returns Response.success(categoryResponse)

        // Act
        val result = categoryRepository.getAllCategories("boi")

        // Assert
        assertEquals(Result.Success(categoryResponse.toCategorieList()), result)
    }

    @Test
    fun `getAllCategories should return error when api response is unsuccessful`() = runTest {
        // Arrange
        coEvery { apiService.getAllCategories("boi", 12) } returns Response.error(
            500,
            "server error".toResponseBody("text/plain".toMediaType())
        )

        // Act
        val result = categoryRepository.getAllCategories("boi")

        // Assert
        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception is HttpException)
    }

    @Test
    fun `saveCategory should send current user id with category`() = runTest {
        // Arrange
        val capturedCategory = mutableListOf<Category>()
        coEvery { apiService.addCategory(capture(capturedCategory)) } returns Response.success(Unit)

        // Act
        val result = categoryRepository.saveCategory(Category(libelle = "Boissons"))

        // Assert
        assertEquals(Result.Success(Unit), result)
        assertEquals(12, capturedCategory.single().userid)
        assertEquals("Boissons", capturedCategory.single().libelle)
    }
}
