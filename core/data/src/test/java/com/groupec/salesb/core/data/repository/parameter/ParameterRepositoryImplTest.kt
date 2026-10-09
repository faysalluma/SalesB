package com.groupec.salesb.core.data.repository.parameter

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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.RequestBody
import okio.Buffer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Response

class ParameterRepositoryImplTest {
    private val store = mockk<DataStoreManager>()
    private val api = mockk<ApiService>()
    private val context = mockk<Context>()
    private val session = MutableStateFlow(UserSession(User(
        7, "Test", "test@example.com", null, null, true, false,
        listOf(UserCompany(108, 8, "OWNER", null, null, "COMMERCE", emptyList()))
    ), 8))
    private lateinit var repository: ParameterRepositoryImpl

    private fun response(companyId: Int = 8) = ParamItemResponse(
        id = 42, logo = null, raisonsociale = "Company", serviceview = 1,
        ifu = null, adresse = null, telephone = null, email = null, website = null,
        devise = "EUR", tva = null, expirationdate = null, offline = null,
        showimageonproduct = 0, useintforpriceandamout = 1, activepaymentmode = 1,
        activeclient = 1, activeprinter = 0, userid = 7, entrepriseid = companyId,
        cashEnabled = 1
    )

    private fun RequestBody.text(): String = Buffer().also { writeTo(it) }.readUtf8()

    @BeforeEach
    fun setup() {
        every { context.getString(any()) } answers { "translated:${firstArg<Int>()}" }
        every { store.userSessionFlow } returns session
        coEvery { store.setParameterOnlyBusinessDataConfig(any()) } returns Unit
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Response.success(ApiResult(data = response()))
        repository = ParameterRepositoryImpl(store, api, context)
    }

    @Test
    fun `update uses selected company and sends empty optional fields to clear them`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            assertEquals(7, arg<Int>(0))
            assertEquals(8, arg<Int>(1))
            assertEquals("SERVICE", arg<RequestBody>(3).text())
            (4..8).forEach { assertEquals("", arg<RequestBody>(it).text()) }
            assertEquals("", arg<RequestBody>(10).text())
            Response.success(ApiResult(data = response()))
        }
        val result = repository.updateBusinessInfo(Parameter(id = 99, entreprisetype = 1, devise = "EUR",
            ifu = null, adresse = null, telephone = null, email = null, website = null), null, null)
        assertTrue(result is Result.Success)
        val parameter = (result as Result.Success).data
        assertEquals(8, parameter.entrepriseid)
        assertTrue(parameter.cashEnabled)
        coVerify { store.setParameterOnlyBusinessDataConfig(parameter) }
    }

    @Test
    fun `backend business error is read by repository`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Response.success(ApiResult(error = true, message = "Rejected"))
        val result = repository.updateBusinessInfo(Parameter(), null, null) as Result.Error
        assertEquals("Rejected", result.exception.message)
        coVerify(exactly = 0) { store.setParameterOnlyBusinessDataConfig(any()) }
    }

    @Test
    fun `wrong company response is not cached`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } returns
            Response.success(ApiResult(data = response(9)))
        assertTrue(repository.updateBusinessInfo(Parameter(), null, null) is Result.Error)
        coVerify(exactly = 0) { store.setParameterOnlyBusinessDataConfig(any()) }
    }

    @Test
    fun `logout during request does not restore company cache`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } coAnswers {
            session.value = UserSession()
            Response.success(ApiResult(data = response()))
        }
        assertTrue(repository.updateBusinessInfo(Parameter(), null, null) is Result.Error)
        coVerify(exactly = 0) { store.setParameterOnlyBusinessDataConfig(any()) }
    }

    @Test
    fun `no selected company does not call API`() = runTest {
        session.value = session.value.copy(selectedCompanyId = null)
        assertTrue(repository.updateBusinessInfo(Parameter(), null, null) is Result.Error)
        coVerify(exactly = 0) { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) }
    }

    @Test
    fun `API cancellation is propagated`() = runTest {
        coEvery { api.updateParameter(any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any(), any()) } throws CancellationException()
        assertThrows<CancellationException> { repository.updateBusinessInfo(Parameter(), null, null) }
    }
}
