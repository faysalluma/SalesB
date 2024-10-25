package com.groupec.salesb.core.data.repository


import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.retrofit.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRemoteRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext val context: Context
) {

    suspend fun checkLogin(email: String, password: String): Result<User> {
        return try {
            val response = apiService.getUserByEmail(email)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    val error = result.error
                    if (error) {
                        Result.Error(Exception(context.getString(R.string.error_user_not_found)))
                    } else {
                        val user = result.data!!
                        if (BCrypt.checkpw(password, user.password)) {
                            Result.Success(user.toUser())
                        } else {
                            Result.Error(Exception(context.getString(R.string.error_invalid_password)))
                        }
                    }
                } ?: Result.Error(Exception(context.getString(R.string.error_empty_response)))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /*suspend fun checkLogin(email: String, password: String) : Flow<NetworkResult<List<Order>>> = flow {
        try {
            val response = apiService.getRandomDog()
            if (response.isSuccessful) {
                val orders = response.body()?.commands?.map { it.toOrder() } ?: emptyList()
                emit(NetworkResult.Success(orders))
            } else {
                emit(NetworkResult.Error(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(NetworkResult.Error(e))
        }
    }.flowOn(Dispatchers.IO)*/
}