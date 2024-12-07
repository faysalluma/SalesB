package com.groupec.salesb.core.network.retrofit.common

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import com.groupec.salesb.core.Result

/* Make output or get actions result */
suspend fun <T, R> safeApiCall(apiCall: suspend () -> Response<T>, transform: (T) -> R): R {
    return withContext(Dispatchers.IO) {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                return@withContext transform(it)
            } ?: throw Exception("Empty response body")
        } else {
            throw HttpException(response)
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