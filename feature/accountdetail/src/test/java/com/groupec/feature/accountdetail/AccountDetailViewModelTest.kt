package com.groupec.feature.accountdetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.parameter.GetParameterUseCase
import com.groupec.salesb.core.domain.user.SaveUserUseCase
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.UserDataForm
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AccountDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveUserUseCase: SaveUserUseCase = mockk()
    private val getParameterUseCase: GetParameterUseCase = mockk()
    private lateinit var viewModel: AccountDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { getParameterUseCase() } returns flowOf(Parameter(devise = "XOF"))
        viewModel = AccountDetailViewModel(saveUserUseCase, getParameterUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addUser should save mapped user and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedUser = mutableListOf<User>()
        coEvery { saveUserUseCase(capture(capturedUser)) } returns Result.Success(Unit)

        // Act
        viewModel.addUser(
            UserDataForm(
                id = "5",
                nomprenom = "Admin",
                adresse = "Cotonou",
                tel = "123",
                email = "admin@salesb.test",
                password = "secret",
                actif = 1,
                privilege = listOf("admin", "sale")
            )
        )
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addUserUiState.value)
        assertEquals(5, capturedUser.single().id)
        assertEquals("Admin", capturedUser.single().nomprenom)
        assertEquals("admin,sale", capturedUser.single().privilege)
        assertEquals(true, capturedUser.single().actif)
        assertEquals(true, capturedUser.single().firstlogin)
        coVerify(exactly = 1) { saveUserUseCase(any()) }
    }

    @Test
    fun `addUser should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { saveUserUseCase(any()) } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.addUser(UserDataForm(nomprenom = "Admin", email = "admin@salesb.test"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.addUserUiState.value)
    }
}
