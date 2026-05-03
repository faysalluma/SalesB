package com.groupec.feature.login

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.user.LoginUseCase
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.Credentials
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
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val loginUseCase: LoginUseCase = mockk()
    private lateinit var viewModel: LoginViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LoginViewModel(loginUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login should emit success with user info when use case succeeds`() = runTest {
        // Arrange
        val user = User(
            nomprenom = "Admin",
            email = "admin@salesb.test",
            password = "secret",
            actif = true,
            firstlogin = false
        )
        coEvery { loginUseCase("admin@salesb.test", "secret") } returns Result.Success(user to true)

        // Act
        viewModel.login(Credentials(email = "admin@salesb.test", password = "secret"))
        advanceUntilIdle()

        // Assert
        assertEquals(LoginUiState.Success(user to true), viewModel.loginUiState.value)
        coVerify(exactly = 1) { loginUseCase("admin@salesb.test", "secret") }
    }

    @Test
    fun `login should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { loginUseCase(any(), any()) } returns Result.Error(IllegalStateException("bad credentials"))

        // Act
        viewModel.login(Credentials(email = "admin@salesb.test", password = "wrong"))
        advanceUntilIdle()

        // Assert
        assertEquals(LoginUiState.Error("bad credentials"), viewModel.loginUiState.value)
    }

    @Test
    fun `resetFlow should emit idle state`() {
        // Act
        viewModel.resetFlow()

        // Assert
        assertEquals(LoginUiState.Idle, viewModel.loginUiState.value)
    }
}
