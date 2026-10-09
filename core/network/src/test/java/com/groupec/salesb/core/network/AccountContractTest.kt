package com.groupec.salesb.core.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.groupec.salesb.core.model.data.Login
import com.groupec.salesb.core.model.data.PasswordChange
import com.groupec.salesb.core.network.model.AccountResponse
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.model.CompaniesResponse
import com.groupec.salesb.core.network.model.CompanyMembershipResponse
import com.groupec.salesb.core.network.retrofit.ApiService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import java.lang.reflect.Modifier

class AccountContractTest {
    private val gson = Gson()
    private val resultType = object : TypeToken<ApiResult<AccountResponse>>() {}.type

    @Test
    fun `login contains credentials without altering password`() {
        val body = gson.toJsonTree(Login("test@example.com", " Pass word ")).asJsonObject

        assertEquals(setOf("email", "password"), body.keySet())
        assertEquals("test@example.com", body["email"].asString)
        assertEquals(" Pass word ", body["password"].asString)
    }

    @Test
    fun `password change uses server field names`() {
        val body = gson.toJsonTree(PasswordChange("test@example.com", "old", "new")).asJsonObject

        assertEquals(setOf("email", "current_password", "new_password"), body.keySet())
        assertEquals("old", body["current_password"].asString)
        assertEquals("new", body["new_password"].asString)
    }

    @Test
    fun `credentials are not exposed through toString`() {
        assertFalse(Login("test@example.com", "secret-password").toString().contains("secret-password"))
        val request = PasswordChange("test@example.com", "old-secret", "new-secret")
        assertFalse(request.toString().contains("old-secret"))
        assertFalse(request.toString().contains("new-secret"))
    }

    @Test
    fun `login reads nullable account details and rights for each company`() {
        val result = gson.fromJson<ApiResult<AccountResponse>>(
            """{
                "error":false,
                "data":{
                    "userid":7,"nomprenom":"Test","email":"test@example.com",
                    "adresse":null,"tel":null,"actif":1,"firstlogin":1,
                    "companies":[
                        {"memberid":3000000000,"entrepriseid":8,"role":"OWNER",
                         "raisonsociale":null,"image":null,"businessType":"COMMERCE",
                         "permissions":["PRODUCT_VIEW","PRODUCT_ARCHIVE"]},
                        {"memberid":9,"entrepriseid":10,"role":"EMPLOYEE",
                         "raisonsociale":"Services","image":null,"businessType":"SERVICE",
                         "permissions":["PRODUCT_VIEW"]}
                    ]
                }
            }""", resultType
        )

        assertFalse(result.error)
        val account = requireNotNull(result.data)
        assertEquals(7, account.userId)
        assertEquals(1, account.firstLogin)
        assertNull(account.address)
        assertNull(account.phone)
        assertEquals(2, account.companies.size)
        assertEquals(3000000000L, account.companies.first().memberId)
        assertNull(account.companies.first().companyName)
        assertEquals(listOf("PRODUCT_VIEW", "PRODUCT_ARCHIVE"), account.companies.first().permissions)
        assertEquals(listOf("PRODUCT_VIEW"), account.companies.last().permissions)
        assertEquals("SERVICE", account.companies.last().businessType)
        val fields = gson.toJsonTree(account).asJsonObject.keySet()
        assertFalse(fields.any { it in setOf("password", "reset_password", "reset_expires", "privilege") })
    }

    @Test
    fun `account without companies remains valid`() {
        val result = gson.fromJson<ApiResult<AccountResponse>>(
            """{"error":false,"data":{"userid":7,"nomprenom":"Test",
                "email":"test@example.com","adresse":null,"tel":null,
                "actif":1,"firstlogin":0,"companies":[]}}""", resultType
        )
        assertTrue(requireNotNull(result.data).companies.isEmpty())
    }

    @Test
    fun `login failure has a message and no account`() {
        val result = gson.fromJson<ApiResult<AccountResponse>>(
            """{"error":true,"message":"Invalid email or password"}""", resultType
        )
        assertTrue(result.error)
        assertNull(result.data)
        assertEquals("Invalid email or password", result.message)
    }

    @Test
    fun `company list has its own response envelope`() {
        val result = gson.fromJson("""{"companies":[]}""", CompaniesResponse::class.java)
        assertTrue(result.companies.isEmpty())
    }

    @Test
    fun `every account contract field declares its JSON name`() {
        val models = listOf(
            Login::class.java, PasswordChange::class.java,
            AccountResponse::class.java, CompanyMembershipResponse::class.java,
            CompaniesResponse::class.java
        )
        models.forEach { model ->
            model.declaredFields.filterNot { Modifier.isStatic(it.modifiers) || it.isSynthetic }
                .forEach { field ->
                    assertTrue(field.isAnnotationPresent(SerializedName::class.java), "${model.simpleName}.${field.name}")
                }
        }
    }

    @Test
    fun `account routes match migrated API and do not place credentials in URL`() {
        val methods = ApiService::class.java.declaredMethods
            .filterNot { it.name == "changePassword" && it.parameterTypes.first() != PasswordChange::class.java }
            .associateBy { it.name }
        assertEquals("checkLogin", requireNotNull(methods.getValue("login").getAnnotation(POST::class.java)).value)
        assertEquals("changePassword", requireNotNull(methods.getValue("changePassword").getAnnotation(PUT::class.java)).value)
        assertEquals("companies/{userid}", requireNotNull(methods.getValue("getCompanies").getAnnotation(GET::class.java)).value)
    }
}
