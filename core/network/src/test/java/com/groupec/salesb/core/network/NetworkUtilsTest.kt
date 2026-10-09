package com.groupec.salesb.core.network

import android.content.Context
import io.mockk.every
import io.mockk.mockk
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCallGetResult
import com.groupec.salesb.core.network.retrofit.common.ApiError
import com.groupec.salesb.core.network.model.ApiResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class NetworkUtilsTest {
    @Test
    fun `executeApiCall keeps HTTP translation when context is supplied`() = runTest {
        val context = mockk<Context>()
        every { context.getString(R.string.error_403_forbidden) } returns "Accès refusé"
        val result = executeApiCall(context = context, errorMessage = "custom error") {
            Response.error<String>(403, """{"message":"Backend message"}""".toResponseBody("application/json".toMediaType()))
        }
        assertEquals("Accès refusé", (result as Result.Error).exception.message)
    }

    @Test
    fun `executeApiCall keeps HttpException without context or custom message`() = runTest {
        val result = executeApiCall {
            Response.error<String>(403, """{"message":"Backend message"}""".toResponseBody("application/json".toMediaType()))
        }
        assertTrue(result is Result.Error)
        assertEquals(403, ((result as Result.Error).exception as HttpException).code())
    }

    @Test
    fun `executeApiCall custom message does not turn successful data into an error`() = runTest {
        val result = executeApiCall(errorMessage = "custom error") { Response.success("ok") }
        assertEquals(Result.Success("ok"), result)
    }

    @Test
    fun `executeApiCall leaves business envelope handling to the repository`() = runTest {
        val body = ApiResult(error = true, data = "ignored", message = "Account disabled")
        val result = executeApiCall(errorMessage = "custom HTTP error") { Response.success(body) }
        assertEquals(Result.Success(body), result)
    }

    @Test
    fun `safeApiCall rejects an API error before transformation`() = runTest {
        var transformed = false
        try {
            safeApiCall({ Response.success(ApiResult(error = true, data = "ignored")) }) {
                transformed = true
                it.data
            }
            fail("Expected API rejection")
        } catch (exception: IllegalStateException) {
            assertEquals(ApiError.API_REJECTED.name, exception.message)
            assertEquals(false, transformed)
        }
    }

    @Test
    fun `safeApiCall rejects missing response body`() = runTest {
        try {
            safeApiCall({ Response.success<String>(null) }) { it }
            fail("Expected missing body error")
        } catch (exception: IllegalStateException) {
            assertEquals(ApiError.EMPTY_RESPONSE.name, exception.message)
        }
    }

    @Test
    fun `safeApiCall should transform successful body`() = runTest {
        val value = safeApiCall(
            apiCall = { Response.success("42") },
            transform = { it.toInt() }
        )

        assertEquals(42, value)
    }

    @Test
    fun `safeApiCall should throw HttpException for unsuccessful response`() = runTest {
        try {
            safeApiCall(
                apiCall = { Response.error<String>(404, "missing".toResponseBody("text/plain".toMediaType())) },
                transform = { it }
            )
            fail("Expected HttpException")
        } catch (exception: HttpException) {
            assertEquals(404, exception.code())
        }
    }

    @Test
    fun `executeApiCall should return success for successful response`() = runTest {
        val result = executeApiCall(apiCall = { Response.success("ok") })

        assertEquals(Result.Success("ok"), result)
    }

    @Test
    fun `executeApiCall should return provided error message when no context`() = runTest {
        val result = executeApiCall(
            errorMessage = "custom error",
            apiCall = { Response.error<String>(409, "conflict".toResponseBody("text/plain".toMediaType())) }
        )

        assertTrue(result is Result.Error)
        assertEquals("custom error", (result as Result.Error).exception.message)
    }

    @Test
    fun `executeApiCall should wrap thrown exception`() = runTest {
        val exception = IOException("offline")

        val result = executeApiCall<String> { throw exception }

        assertEquals(Result.Error(exception), result)
    }

    @Test
    fun `safeApiCallGetResult should return default when call fails`() = runTest {
        val value = safeApiCallGetResult(
            apiCall = { throw IOException("offline") },
            transform = { body: String -> body.toInt() },
            default = -1
        )

        assertEquals(-1, value)
    }
}
