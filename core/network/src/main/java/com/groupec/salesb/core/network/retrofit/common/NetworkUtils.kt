package com.groupec.salesb.core.network.retrofit.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import com.groupec.salesb.core.Result
import java.io.IOException


suspend fun <T, R> safeApiCall(apiCall: suspend () -> Response<T>, transform: (T) -> R): R {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@withContext transform(it)
                } ?: throw Exception("Empty response body")
            } else {
                throw HttpException(response)
            }
        } catch (exception: IOException) { // Handle network errors
            throw exception
        } catch (exception: Exception) { // Handle other errors
            throw exception
        }
    }
}

/* Make input actions and get body result */
suspend fun <T> executeApiCall(apiCall: suspend () -> Response<T>, errorMessage: String? = null): Result<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            Result.Error(errorMessage?.let { Exception(it)} ?: HttpException(response))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }
}

suspend fun <T, R> safeApiCallGetResult(
    apiCall: suspend () -> Response<T>,
    transform: (T) -> R,
    default: R
): R {
    val result = apiCallResult(apiCall = apiCall, transform = transform)
    return when (result) {
        is Result.Success -> result.data
        is Result.Error -> {
            println(result.exception)
            default
        }
        else -> {default}
    }
}

private suspend fun <T, R> apiCallResult(
    apiCall: suspend () -> Response<T>,
    transform: (T) -> R
): Result<R> {
    return withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.Success(transform(body))
                } else {
                    Result.Error(Exception("Empty response body"))
                }
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: IOException) { // Gestion des erreurs réseau
            Result.Error(e)
        } catch (e: Exception) { // Gestion des autres erreurs
            Result.Error(e)
        }
    }
}