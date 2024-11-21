package com.groupec.salesb.core.data.repository.common


import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.data.model.toUserStore
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserRemoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
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
                        if (user.actif == 0) {
                            Result.Error(Exception(context.getString(R.string.error_user_not_active)))
                        } else {
                            if (BCrypt.checkpw(password, user.password)) {
                                dataStoreManager.setUserConfig(user.toUserStore())
                                Result.Success(user.toUser())
                            } else {
                                Result.Error(Exception(context.getString(R.string.error_invalid_password)))
                            }
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

    suspend fun changePassword(userId: Int, ancPassword: String, password: String): Result<User> {
        return try {
            val response = apiService.getUserById(userId)
            if (response.isSuccessful) {
                response.body()?.let { result ->
                    val error = result.error
                    if (error) {
                        Result.Error(Exception(context.getString(R.string.error_user_not_found)))
                    } else {
                        val user = result.data!!
                        updatePassword(user.toUser(), ancPassword, password)
                    }
                } ?: Result.Error(Exception(context.getString(R.string.error_empty_response)))
            } else {
                Result.Error(HttpException(response))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


    private suspend fun updatePassword(user: User, ancPassword: String, password: String): Result<User> {
        return when {
            !user.firstlogin && user.password != ancPassword -> {
                Result.Error(Exception(context.getString(R.string.error_bad_anc_password)))
            }
            else -> {
                // Hash pawword with salt generating
                val hashPassword = BCrypt.hashpw(password, BCrypt.gensalt())
                val updatedUser = user.copy(
                    password = hashPassword,
                    firstlogin = false,
                    datemodif = currentDateString(),
                    synchronised = true
                )

                executeApiCall(
                    apiCall = {
                        apiService.changePassword(user.id, updatedUser)
                    },
                    errorMessage = context.getString(R.string.error_updating_data)
                )
            }
        }
    }

    /*
    suspend fun checkLogin(email: String, password: String) : Flow<NetworkResult<List<Order>>> = flow {
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