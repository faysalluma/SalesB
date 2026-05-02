package com.groupec.salesb.core.domain

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import com.groupec.salesb.core.data.repository.product.ProductRepository
import com.groupec.salesb.core.data.repository.product.StatisticRepository
import com.groupec.salesb.core.domain.category.DeleteCategoryUseCase
import com.groupec.salesb.core.domain.category.GetAllCategoriesUseCase
import com.groupec.salesb.core.domain.category.GetCategoryUseCase
import com.groupec.salesb.core.domain.category.SaveCategoryUseCase
import com.groupec.salesb.core.domain.parameter.AcceptTermsAndConditionsUseCase
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import com.groupec.salesb.core.domain.parameter.UpdateBusinessInfoUseCase
import com.groupec.salesb.core.domain.parameter.UpdateFirstLoginParameterUseCase
import com.groupec.salesb.core.domain.product.DeleteProductUseCase
import com.groupec.salesb.core.domain.product.GetAllProductsUseCase
import com.groupec.salesb.core.domain.product.SaveProductUseCase
import com.groupec.salesb.core.domain.sale.GetTotalSaleUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalAmountOutputUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalAmountSaleUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalSaleByDateUseCase
import com.groupec.salesb.core.domain.statistic.GetTotalSaleDayUseCase
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DomainUseCasesTest {

    @Test
    fun `GetCategoryUseCase should emit categories when repository returns data`() = runTest {
        // Arrange
        val repository: CategoryRepository = mockk()
        val expectedCategories = listOf(Category(id = 3, libelle = "Boissons"))
        every { repository.getCategories() } returns flowOf(expectedCategories)

        // Act
        val result = GetCategoryUseCase(repository).invoke().first()

        // Assert
        assertEquals(expectedCategories, result)
        verify(exactly = 1) { repository.getCategories() }
    }

    @Test
    fun `GetCategoryUseCase should throw exception when repository flow throws`() = runTest {
        // Arrange
        val repository: CategoryRepository = mockk()
        every { repository.getCategories() } returns flow { throw RuntimeException("Failed to fetch categories") }

        // Act
        val result = GetCategoryUseCase(repository).invoke()

        // Assert
        assertThrows<RuntimeException> {
            result.first()
        }.also { thrown ->
            assertEquals("Failed to fetch categories", thrown.message)
        }
    }

    @Test
    fun `category command use cases should delegate to repository`() = runTest {
        // Arrange
        val repository: CategoryRepository = mockk()
        val category = Category(id = 1, libelle = "Boissons")
        coEvery { repository.getAllCategories("boi") } returns Result.Success(listOf(category))
        coEvery { repository.saveCategory(category) } returns Result.Success(Unit)
        coEvery { repository.deleteCategory(1) } returns Result.Success(Unit)

        // Act
        val allCategories = GetAllCategoriesUseCase(repository).invoke("boi")
        val saveResult = SaveCategoryUseCase(repository).invoke(category)
        val deleteResult = DeleteCategoryUseCase(repository).invoke(1)

        // Assert
        assertEquals(Result.Success(listOf(category)), allCategories)
        assertEquals(Result.Success(Unit), saveResult)
        assertEquals(Result.Success(Unit), deleteResult)
        coVerify(exactly = 1) { repository.getAllCategories("boi") }
        coVerify(exactly = 1) { repository.saveCategory(category) }
        coVerify(exactly = 1) { repository.deleteCategory(1) }
    }

    @Test
    fun `GetAllCategoriesUseCase should return error when repository throws`() = runTest {
        // Arrange
        val repository: CategoryRepository = mockk()
        val exception = IllegalStateException("network")
        coEvery { repository.getAllCategories("any") } throws exception

        // Act
        val result = GetAllCategoriesUseCase(repository).invoke("any")

        // Assert
        assertEquals(Result.Error(exception), result)
    }

    @Test
    fun `product use cases should delegate search save and delete arguments`() = runTest {
        // Arrange
        val repository: ProductRepository = mockk()
        val product = Product(id = 8, libelle = "Café", prixttc = 1200.0)
        coEvery { repository.getProducts("caf") } returns Result.Success(listOf(product))
        coEvery { repository.saveProduct(product, null) } returns Result.Success(Unit)
        coEvery { repository.deleteProduct(8) } returns Result.Success(Unit)

        // Act
        val products = GetAllProductsUseCase(repository).invoke("caf")
        val saveResult = SaveProductUseCase(repository).invoke(product, null)
        val deleteResult = DeleteProductUseCase(repository).invoke(8)

        // Assert
        assertEquals(Result.Success(listOf(product)), products)
        assertEquals(Result.Success(Unit), saveResult)
        assertEquals(Result.Success(Unit), deleteResult)
        coVerify(exactly = 1) { repository.getProducts("caf") }
        coVerify(exactly = 1) { repository.saveProduct(product, null) }
        coVerify(exactly = 1) { repository.deleteProduct(8) }
    }

    @Test
    fun `statistic use cases should delegate date ranges`() = runTest {
        // Arrange
        val repository: StatisticRepository = mockk()
        every { repository.getTotalSales("2026-05-01", "2026-05-31") } returns flowOf(12)
        every { repository.getTotalAmountSales("2026-05-01", "2026-05-31") } returns flowOf(1234.0)
        every { repository.getTotalAmountOutputs("2026-05-01", "2026-05-31") } returns flowOf(50.0)
        every { repository.getTotalSaleMorningEvening("2026-05-01") } returns flowOf(40.0 to 60.0)
        every { repository.getTotalSalesByDate("2026-05-01", "2026-05-31") } returns flowOf(
            listOf("2026-05-01" to 100.0)
        )

        // Act & Assert
        assertEquals(12, GetTotalSaleUseCase(repository).invoke("2026-05-01", "2026-05-31").first())
        assertEquals(1234.0, GetTotalAmountSaleUseCase(repository).invoke("2026-05-01", "2026-05-31").first())
        assertEquals(50.0, GetTotalAmountOutputUseCase(repository).invoke("2026-05-01", "2026-05-31").first())
        assertEquals(40.0 to 60.0, GetTotalSaleDayUseCase(repository).invoke("2026-05-01").first())
        assertEquals(
            listOf("2026-05-01" to 100.0),
            GetTotalSaleByDateUseCase(repository).invoke("2026-05-01", "2026-05-31").first()
        )
    }

    @Test
    fun `parameter use cases should delegate to repository`() = runTest {
        // Arrange
        val repository: ParameterRepository = mockk()
        val initial = Parameter(id = 1, raisonsociale = "Old")
        val updated = initial.copy(raisonsociale = "New", tva = 18.0)
        every { repository.getParameters() } returns flowOf(initial)
        coEvery { repository.saveParameters(initial) } returns Result.Success(Unit)
        coEvery { repository.updateBusinessInfo(updated, null, "18") } returns Result.Success(updated)
        coEvery { repository.updateFirstLogin() } returns Result.Success(Unit)
        coEvery { repository.acceptTermsAndConditions() } returns Result.Success(Unit)

        // Act
        val parameter = GetParameterUseCase(repository).invoke().first()
        val saveResult = SaveParameterUseCase(repository).invoke(initial)
        val updateResult = UpdateBusinessInfoUseCase(repository).invoke(updated, logoUri = null, tva = "18")
        val firstLoginResult = UpdateFirstLoginParameterUseCase(repository).invoke()
        val acceptTermsResult = AcceptTermsAndConditionsUseCase(repository).invoke()

        // Assert
        assertEquals(initial, parameter)
        assertEquals(Result.Success(Unit), saveResult)
        assertEquals(Result.Success(updated), updateResult)
        assertEquals(Result.Success(Unit), firstLoginResult)
        assertEquals(Result.Success(Unit), acceptTermsResult)
        verify(exactly = 1) { repository.getParameters() }
        coVerify(exactly = 1) { repository.saveParameters(initial) }
        coVerify(exactly = 1) { repository.updateBusinessInfo(updated, null, "18") }
        coVerify(exactly = 1) { repository.updateFirstLogin() }
        coVerify(exactly = 1) { repository.acceptTermsAndConditions() }
    }
}
