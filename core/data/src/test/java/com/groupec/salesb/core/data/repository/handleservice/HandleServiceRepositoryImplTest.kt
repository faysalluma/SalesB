package com.groupec.salesb.core.data.repository.handleservice

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserCompany
import com.groupec.salesb.core.model.data.UserSession
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.model.ParamItemResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import okio.Buffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response

class HandleServiceRepositoryImplTest {
    private val store = mockk<DataStoreManager>()
    private val api = mockk<ApiService>()
    private val context = mockk<Context>()
    private val response = mockk<ParamItemResponse>(relaxed = true)
    private lateinit var repository: HandleServiceRepositoryImpl

    @BeforeEach
    fun setup() {
        every { context.getString(any()) } answers { "translated:${firstArg<Int>()}" }
        every { store.userSessionFlow } returns flowOf(UserSession(User(7, "Test", "test@example.com",
            null, null, true, false, listOf(UserCompany(108, 8, "OWNER", null, null, "COMMERCE", emptyList()))), 8))
        every { store.parameterFlow } returns flowOf(Parameter(userid = 7, entrepriseid = 8, devise = "EUR"))
        every { response.userid } returns 7
        every { response.entrepriseid } returns 8
        coEvery { store.setParameterConfig(any()) } returns Unit
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Response.success(ApiResult(data = response))
        repository = HandleServiceRepositoryImpl(store, api, context)
    }

    @Test
    fun `cash update sends only cash option and omits optional identity fields`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            assertEquals(7, arg<Int>(0))
            assertEquals(8, arg<Int>(1))
            assertEquals(null, arg<RequestBody?>(2))
            (4..8).forEach { assertEquals(null, arg<RequestBody?>(it)) }
            assertEquals(null, arg<RequestBody?>(10))
            val options = arg<Map<String, RequestBody>>(12)
            assertEquals(setOf("cash_enabled"), options.keys)
            assertEquals("1", Buffer().also { options.getValue("cash_enabled").writeTo(it) }.readUtf8())
            Response.success(ApiResult(data = response))
        }
        assertTrue(repository.updateCashEnabled(true) is Result.Success)
        coVerify(exactly = 1) { store.setParameterConfig(any()) }
    }

    @Test
    fun `business rejection does not modify local configuration`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Response.success(ApiResult(error = true, message = "Rejected"))
        assertEquals("Rejected", (repository.updateActiveClient(false) as Result.Error).exception.message)
        coVerify(exactly = 0) { store.setParameterConfig(any()) }
    }

    @Test
    fun `response from another company is not cached`() = runTest {
        every { response.entrepriseid } returns 9
        assertTrue(repository.updateCashEnabled(true) is Result.Error)
        coVerify(exactly = 0) { store.setParameterConfig(any()) }
    }
}
