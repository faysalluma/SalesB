package com.groupec.feature.clientdetail

import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.domain.client.SaveClientUseCase
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.ui.ClientDataForm
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
class ClientDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val saveClientUseCase: SaveClientUseCase = mockk()
    private lateinit var viewModel: ClientDetailViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ClientDetailViewModel(saveClientUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `addClient should save mapped client and emit success when use case succeeds`() = runTest {
        // Arrange
        val capturedClient = mutableListOf<Client>()
        coEvery { saveClientUseCase(capture(capturedClient)) } returns Result.Success(Unit)

        // Act
        viewModel.addClient(
            ClientDataForm(id = "4", nomprenom = "Client A", adresse = "", telephone = "123")
        )
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Success(Unit), viewModel.addClientUiState.value)
        assertEquals(4, capturedClient.single().id)
        assertEquals("Client A", capturedClient.single().nomprenom)
        assertNull(capturedClient.single().adresse)
        assertEquals("123", capturedClient.single().telephone)
        coVerify(exactly = 1) { saveClientUseCase(any()) }
    }

    @Test
    fun `addClient should emit error when use case fails`() = runTest {
        // Arrange
        coEvery { saveClientUseCase(any()) } returns Result.Error(IllegalStateException("save failed"))

        // Act
        viewModel.addClient(ClientDataForm(nomprenom = "Client A"))
        advanceUntilIdle()

        // Assert
        assertEquals(FormUIState.Error("save failed"), viewModel.addClientUiState.value)
    }
}
