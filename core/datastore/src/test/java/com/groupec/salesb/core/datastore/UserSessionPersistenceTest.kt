package com.groupec.salesb.core.datastore

import android.content.Context
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserSession
import com.groupec.salesb.core.model.data.UserCompany
import com.groupec.salesb.core.model.data.Parameter
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class UserSessionPersistenceTest {
    @Test
    fun `business update preserves local switches and stores optional cash configuration`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        manager.setParameterConfig(Parameter(userid = 7, entrepriseid = 8, showimageonproduct = true))
        manager.setParameterOnlyBusinessDataConfig(Parameter(userid = 7, entrepriseid = 8, cashEnabled = true))
        assertEquals(8, manager.parameterFlow.first().entrepriseid)
        assertEquals(true, manager.parameterFlow.first().cashEnabled)
        assertEquals(true, manager.parameterFlow.first().showimageonproduct)
        manager.setUserSession(session(9))
        assertNull(manager.parameterFlow.first().entrepriseid)
        assertFalse(manager.parameterFlow.first().cashEnabled)
    }

    @Test
    fun `late business update cannot overwrite selected company configuration`() = runTest {
        val manager = manager()
        manager.setUserSession(session(9))
        assertThrows<IllegalArgumentException> {
            manager.setParameterOnlyBusinessDataConfig(Parameter(userid = 7, entrepriseid = 8, devise = "EUR"))
        }
        assertEquals("", manager.parameterFlow.first().devise)
    }

    @TempDir
    lateinit var directory: Path

    private fun manager(): DataStoreManager {
        val context = mockk<Context>()
        every { context.applicationContext } returns context
        every { context.filesDir } returns directory.toFile()
        return DataStoreManager(context)
    }

    private fun session(companyId: Int = 8): UserSession = UserSession(
        User(7, "Test", "test@example.com", null, null, true, true, listOf(
            UserCompany(108, 8, "OWNER", null, null, "COMMERCE", listOf("PRODUCT_VIEW")),
            UserCompany(109, 9, "EMPLOYEE", "Services", null, "SERVICE", emptyList())
        )), companyId
    )

    @Test
    fun `account and company rights survive preferences serialization`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        assertEquals(session(), manager.userSessionFlow.first())
        assertNull(manager.userSessionFlow.first().account?.address)
    }

    @Test
    fun `changing company clears previous company configuration and pro cache`() = runTest {
        val manager = manager()
        manager.setUserSession(session(8))
        manager.setParameterConfig(Parameter(id = 42, devise = "EUR", raisonsociale = "Old"))
        manager.saveUserSubscriptionStatus(true)
        manager.setUserSession(session(9))
        assertEquals(0, manager.parameterFlow.first().id)
        assertEquals("", manager.parameterFlow.first().devise)
        assertFalse(manager.userFlow.first().isProActive)
        assertEquals(9, manager.userSessionFlow.first().selectedCompanyId)
    }

    @Test
    fun `refreshing same account and company preserves company configuration`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        manager.setParameterConfig(Parameter(id = 42, devise = "EUR"))
        manager.setUserSession(session().copy(account = session().account?.copy(firstLogin = false)))
        assertEquals(42, manager.parameterFlow.first().id)
        assertEquals("EUR", manager.parameterFlow.first().devise)
    }

    @Test
    fun `logout removes account company configuration and legacy credentials`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        manager.setParameterConfig(Parameter(id = 42))
        manager.saveUserSubscriptionStatus(true)
        manager.logout()
        assertNull(manager.userSessionFlow.first().account)
        assertEquals(0, manager.parameterFlow.first().id)
        assertFalse(manager.isLoggedIn())
    }

    @Test
    fun `changing user with same company id clears previous configuration`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        manager.setParameterConfig(Parameter(id = 42))
        manager.setUserSession(session().copy(account = session().account?.copy(userId = 99)))
        assertEquals(0, manager.parameterFlow.first().id)
        assertEquals(99, manager.userSessionFlow.first().account?.userId)
    }

    @Test
    fun `optional values cleared on refresh stay null rather than empty strings`() = runTest {
        val manager = manager()
        val populated = session().copy(account = session().account?.copy(address = "Address", phone = "Phone"))
        manager.setUserSession(populated)
        manager.setUserSession(session())
        assertNull(manager.userSessionFlow.first().account?.address)
        assertNull(manager.userSessionFlow.first().account?.phone)
    }

    @Test
    fun `shrinking company list removes stale companies and keeps permission set`() = runTest {
        val manager = manager()
        manager.setUserSession(session())
        val company = requireNotNull(session().account).companies.last().copy(permissions = listOf("SALE_VIEW", "PRODUCT_VIEW"))
        val reduced = session(9).copy(account = session().account?.copy(companies = listOf(company)))
        manager.setUserSession(reduced)
        val stored = requireNotNull(manager.userSessionFlow.first().account)
        assertEquals(listOf(9), stored.companies.map { it.companyId })
        assertEquals(setOf("SALE_VIEW", "PRODUCT_VIEW"), stored.companies.single().permissions.toSet())
    }

    @Test
    fun `cannot persist a selected company outside account memberships`() = runTest {
        val manager = manager()
        assertThrows<IllegalArgumentException> { manager.setUserSession(session(99)) }
        assertNull(manager.userSessionFlow.first().account)
    }
}
