package com.groupec.feature.signup

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.SaveParameterUseCase
import com.groupec.salesb.core.domain.signup.SaveSignupConfigurationUseCase
import com.groupec.salesb.core.domain.user.CheckUserEmailExistsUseCase
import com.groupec.salesb.core.domain.user.SaveUserStoreByIdUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.SignupConfiguration
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
class SignupViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveSignupConfigurationUseCase: SaveSignupConfigurationUseCase = mockk()
    private val saveParameterUseCase: SaveParameterUseCase = mockk()
    private val checkUserEmailExistsUseCase: CheckUserEmailExistsUseCase = mockk()
    private val saveUserStoreByIdUseCase: SaveUserStoreByIdUseCase = mockk()
    private lateinit var viewModel: SignupViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SignupViewModel(
            saveSignupConfigurationUseCase,
            saveParameterUseCase,
            checkUserEmailExistsUseCase,
            saveUserStoreByIdUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveInitialConfiguration should save parameter and user store when signup succeeds`() = runTest {
        // Arrange
        val configuration = signupConfiguration()
        val parameter = Parameter(id = 1, userid = 42, raisonsociale = "SalesB")
        coEvery { saveSignupConfigurationUseCase(configuration, null) } returns Result.Success(parameter)
        coEvery { saveParameterUseCase(parameter) } returns Result.Success(Unit)
        coEvery { saveUserStoreByIdUseCase(42) } returns Result.Success(Unit)

        // Act
        viewModel.saveInitialConfiguration(configuration, null)
        advanceUntilIdle()

        // Assert
        assertEquals(SignupConfigurationUiState.Success, viewModel.signupConfigurationUiState.value)
        coVerify(exactly = 1) { saveSignupConfigurationUseCase(configuration, null) }
        coVerify(exactly = 1) { saveParameterUseCase(parameter) }
        coVerify(exactly = 1) { saveUserStoreByIdUseCase(42) }
    }

    @Test
    fun `saveInitialConfiguration should emit error when signup response has no user id`() = runTest {
        // Arrange
        val configuration = signupConfiguration()
        val parameter = Parameter(id = 1, userid = null, raisonsociale = "SalesB")
        coEvery { saveSignupConfigurationUseCase(configuration, null) } returns Result.Success(parameter)
        coEvery { saveParameterUseCase(parameter) } returns Result.Success(Unit)

        // Act
        viewModel.saveInitialConfiguration(configuration, null)
        advanceUntilIdle()

        // Assert
        assertEquals(
            SignupConfigurationUiState.Error("User id not found in signup response"),
            viewModel.signupConfigurationUiState.value
        )
    }

    @Test
    fun `checkEmailExists should return use case result`() = runTest {
        // Arrange
        coEvery { checkUserEmailExistsUseCase("user@salesb.test") } returns Result.Success(true)

        // Act
        val result = viewModel.checkEmailExists("user@salesb.test")

        // Assert
        assertEquals(Result.Success(true), result)
    }

    private fun signupConfiguration(): SignupConfiguration {
        return SignupConfiguration(
            fullName = "Admin",
            email = "admin@salesb.test",
            password = "secret",
            companyName = "SalesB",
            companyType = 1,
            companyEmail = "company@salesb.test",
            address = "Cotonou",
            phone = "123",
            ifu = "IFU",
            website = null,
            devise = "XOF",
            tva = 18.0,
            useIntForPriceAndAmount = 1,
            showImageOnProduct = 1,
            activePaymentMode = 1,
            defaultpayment = "cash",
            activePrinter = 0
        )
    }
}
