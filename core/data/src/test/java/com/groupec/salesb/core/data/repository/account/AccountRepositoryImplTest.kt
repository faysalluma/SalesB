package com.groupec.salesb.core.data.repository.account

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toAuthenticatedUser
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.UserSession
import com.groupec.salesb.core.model.data.Login
import com.groupec.salesb.core.model.data.PasswordChange
import com.groupec.salesb.core.network.model.AccountResponse
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.model.CompaniesResponse
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.model.ParamItemResponse
import com.groupec.salesb.core.network.model.CompanyMembershipResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.R
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import retrofit2.Response

class AccountRepositoryImplTest {
    private val api: ApiService = mockk()
    private val context: Context = mockk()
    private val store: DataStoreManager = mockk()
    private val session = MutableStateFlow(UserSession())
    private lateinit var repository: AccountRepositoryImpl

    private fun membership(companyId: Int, permissions: List<String> = listOf("PRODUCT_VIEW")) =
        CompanyMembershipResponse(companyId.toLong() + 100, companyId, "EMPLOYEE", null, null, "COMMERCE", permissions)

    private fun account(companies: List<CompanyMembershipResponse> = listOf(membership(8)), firstLogin: Int = 0) =
        AccountResponse(7, "Test", "test@example.com", null, null, 1, firstLogin, companies)

    @BeforeEach
    fun setup() {
        every { context.getString(any()) } answers { "translated:${firstArg<Int>()}" }
        every { store.userSessionFlow } returns session
        coEvery { store.setUserSession(any()) } coAnswers { session.value = firstArg() }
        coEvery { store.setParameterConfig(any()) } returns Unit
        coEvery { api.getParameter(any(), any()) } returns Response.success(ParameterResponse(mockk<ParamItemResponse>(relaxed = true)))
        repository = AccountRepositoryImpl(api, store, context)
    }

    @Test
    fun `failed company configuration load does not replace session`() = runTest {
        val previous = UserSession(account().toAuthenticatedUser(), 8)
        session.value = previous
        coEvery { api.login(any()) } returns Response.success(ApiResult(data = account()))
        coEvery { api.getParameter(7, 8) } returns Response.error(
            403, "{}".toResponseBody("application/json".toMediaType())
        )
        assertTrue(repository.login("test@example.com", "password") is Result.Error)
        assertEquals(previous, session.value)
        coVerify(exactly = 0) { store.setUserSession(any()) }
        coVerify(exactly = 0) { store.setParameterConfig(any()) }
    }

    @Test
    fun `login normalizes email but leaves password intact and selects sole company`() = runTest {
        val sent = slot<Login>()
        coEvery { api.login(capture(sent)) } returns Response.success(ApiResult(data = account(firstLogin = 1)))

        val result = repository.login(" TEST@Example.COM ", " Pass word ")

        assertEquals(Login("test@example.com", " Pass word "), sent.captured)
        assertEquals(Result.Success(session.value), result)
        assertEquals(8, session.value.selectedCompanyId)
        assertEquals(listOf("PRODUCT_VIEW"), session.value.selectedCompany?.permissions)
        assertTrue(requireNotNull(session.value.account).firstLogin)
        assertNull(session.value.account?.address)
        coVerify(exactly = 1) { api.getParameter(7, 8) }
        coVerify(exactly = 1) { store.setParameterConfig(any()) }
    }

    @Test
    fun `multiple companies require a choice and do not inherit old selection`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        coEvery { api.login(any()) } returns Response.success(ApiResult(data = account(listOf(membership(8), membership(9)))))

        assertTrue(repository.login("test@example.com", "password") is Result.Success)
        assertNull(session.value.selectedCompanyId)
        assertEquals(2, session.value.account?.companies?.size)
    }

    @Test
    fun `account without companies can authenticate without selected company`() = runTest {
        coEvery { api.login(any()) } returns Response.success(ApiResult(data = account(emptyList())))
        assertTrue(repository.login("test@example.com", "password") is Result.Success)
        assertNull(session.value.selectedCompany)
        assertTrue(requireNotNull(session.value.account).companies.isEmpty())
    }

    @Test
    fun `HTTP rejection does not overwrite stored session`() = runTest {
        val previous = UserSession(account().toAuthenticatedUser(), 8)
        session.value = previous
        coEvery { api.login(any()) } returns Response.error(401, "{}".toResponseBody("application/json".toMediaType()))
        val result = repository.login("test@example.com", "bad")
        assertError(result, R.string.error_401_unauthorized)
        assertEquals(previous, session.value)
        coVerify(exactly = 0) { store.setUserSession(any()) }
    }

    @Test
    fun `empty login body is rejected`() = runTest {
        coEvery { api.login(any()) } returns Response.success(null)
        assertError(repository.login("test@example.com", "password"), R.string.error_empty_response)
    }

    @Test
    fun `API error cannot create a session even with data`() = runTest {
        coEvery { api.login(any()) } returns Response.success(ApiResult(error = true, data = account()))
        assertError(repository.login("test@example.com", "password"), R.string.error_api_rejected)
        coVerify(exactly = 0) { store.setUserSession(any()) }
    }

    @Test
    fun `repository preserves business error message without saving account`() = runTest {
        coEvery { api.login(any()) } returns Response.success(
            ApiResult(error = true, data = account(), message = "Account access revoked")
        )
        val result = repository.login("test@example.com", "password")
        assertTrue(result is Result.Error)
        assertEquals("Account access revoked", (result as Result.Error).exception.message)
        coVerify(exactly = 0) { store.setUserSession(any()) }
    }

    @Test
    fun `disabled account is rejected`() = runTest {
        coEvery { api.login(any()) } returns Response.success(ApiResult(data = account().copy(active = 0)))
        assertError(repository.login("test@example.com", "password"), R.string.error_account_disabled)
    }

    @Test
    fun `password change sends credentials to API and clears first login using returned account`() = runTest {
        session.value = UserSession(account(firstLogin = 1).toAuthenticatedUser(), 8)
        val sent = slot<PasswordChange>()
        coEvery { api.changePassword(capture(sent)) } returns Response.success(ApiResult(data = account()))

        assertTrue(repository.changePassword("temporary", "new-password") is Result.Success)
        assertEquals(PasswordChange("test@example.com", "temporary", "new-password"), sent.captured)
        assertFalse(requireNotNull(session.value.account).firstLogin)
        assertEquals(8, session.value.selectedCompanyId)
    }

    @Test
    fun `failed password change leaves first login unchanged`() = runTest {
        session.value = UserSession(account(firstLogin = 1).toAuthenticatedUser(), 8)
        coEvery { api.changePassword(any<PasswordChange>()) } returns Response.error(
            401, "{}".toResponseBody("application/json".toMediaType())
        )
        assertTrue(repository.changePassword("bad", "new-password") is Result.Error)
        assertTrue(requireNotNull(session.value.account).firstLogin)
    }

    @Test
    fun `password change requires a current account`() = runTest {
        assertError(repository.changePassword("old", "new-password"), R.string.error_no_account)
        coVerify(exactly = 0) { api.changePassword(any<PasswordChange>()) }
    }

    @Test
    fun `refresh removes revoked selection and updates company permissions`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        coEvery { api.getCompanies(7) } returns Response.success(CompaniesResponse(listOf(
            membership(9, listOf("SALE_VIEW")), membership(10, emptyList())
        )))
        assertTrue(repository.refreshCompanies() is Result.Success)
        assertNull(session.value.selectedCompanyId)
        assertEquals(listOf("SALE_VIEW"), session.value.account?.companies?.first()?.permissions)
    }

    @Test
    fun `refresh preserves valid selected company`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        coEvery { api.getCompanies(7) } returns Response.success(CompaniesResponse(listOf(membership(8), membership(9))))
        assertTrue(repository.refreshCompanies() is Result.Success)
        assertEquals(8, session.value.selectedCompanyId)
    }

    @Test
    fun `company choice is checked against fresh active memberships`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        coEvery { api.getCompanies(7) } returns Response.success(CompaniesResponse(listOf(membership(8), membership(9))))
        assertTrue(repository.selectCompany(9) is Result.Success)
        assertEquals(9, session.value.selectedCompanyId)
        coVerify(exactly = 1) { api.getCompanies(7) }
    }

    @Test
    fun `unknown company cannot be selected`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        coEvery { api.getCompanies(7) } returns Response.success(CompaniesResponse(listOf(membership(8))))
        assertError(repository.selectCompany(99), R.string.error_company_not_available)
        assertEquals(8, session.value.selectedCompanyId)
    }

    @Test
    fun `cancellation propagates rather than appearing as login failure`() = runTest {
        coEvery { api.login(any()) } throws CancellationException()
        assertThrows<CancellationException> { repository.login("test@example.com", "password") }
        coVerify(exactly = 0) { store.setUserSession(any()) }
    }

    @Test
    fun `repository propagates cancellation wrapped by existing executeApiCall`() = runTest {
        session.value = UserSession(account().toAuthenticatedUser(), 8)
        val previous = session.value
        coEvery { api.getCompanies(7) } throws CancellationException()
        assertThrows<CancellationException> { repository.refreshCompanies() }
        assertEquals(previous, session.value)
        coVerify(exactly = 0) { store.setUserSession(any()) }
    }

    @Test
    fun `logout clears persistent session through datastore`() = runTest {
        coEvery { store.logout() } coAnswers { session.value = UserSession() }
        assertEquals(Result.Success(Unit), repository.logout())
        coVerify(exactly = 1) { store.logout() }
        assertNull(session.value.account)
    }

    private fun assertError(result: Result<*>, messageId: Int) {
        assertTrue(result is Result.Error)
        assertEquals(context.getString(messageId), (result as Result.Error).exception.message)
    }
}
