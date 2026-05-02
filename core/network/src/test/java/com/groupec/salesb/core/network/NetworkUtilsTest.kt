package com.groupec.salesb.core.network

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCallGetResult
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
