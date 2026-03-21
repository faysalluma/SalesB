package com.groupec.salesb.core.data.repository.common


import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toParameter
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.data.model.toUserStore
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.fixBCryptHash
import com.groupec.salesb.core.getDateTimeByNtp
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun checkLogin(email: String, password: String): Result<Pair<User, Boolean>> {
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
                            val isMainPasswordValid = BCrypt.checkpw(password, user.password)
                            val isResetPasswordValid =user.reset_password?.let {
                                BCrypt.checkpw(password, it.fixBCryptHash())
                            } ?: false
                            val isResetPasswordExpired = user.reset_expires?.let {
                                it <= getDateTimeByNtp()
                            } ?: false
                            if (isMainPasswordValid || (isResetPasswordValid && !isResetPasswordExpired)) {
                                when (val parameterResult = chargeParameterIfMissing(user.id)) {
                                    is Result.Error -> Result.Error(parameterResult.exception)
                                    else -> {
                                        dataStoreManager.setUserConfig(user.toUserStore())
                                        Result.Success(user.toUser() to isMainPasswordValid)
                                    }
                                }
                            } else if (isResetPasswordValid) {
                                Result.Error(Exception(context.getString(R.string.error_tempory_password_expire)))
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

    private suspend fun chargeParameterIfMissing(userId: Int?): Result<Unit> {
        val currentParameter = dataStoreManager.parameterFlow.firstOrNull()
        val missingConfig = currentParameter == null ||
            currentParameter.raisonsociale.isBlank() ||
            currentParameter.devise.isBlank()

        if (!missingConfig) {
            return Result.Success(Unit)
        }

        return try {
            val response = apiService.getParameter(userId ?: 0)
            if (!response.isSuccessful) {
                return Result.Error(HttpException(response))
            }

            val parameter = response.body()?.toParameter()
                ?: return Result.Error(Exception(context.getString(R.string.error_empty_response)))

            dataStoreManager.setParameterConfig(parameter)
            Result.Success(Unit)
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

    private suspend fun updatePassword(
        user: User,
        ancPassword: String,
        password: String
    ): Result<User> {
        return when {
            !user.firstlogin && !BCrypt.checkpw(ancPassword, user.password) && user.reset_password == null -> {
                Result.Error(Exception(context.getString(R.string.error_bad_anc_password)))
            }

            else -> {
                // Hash pawword with salt generating
                val hashPassword = BCrypt.hashpw(password, BCrypt.gensalt())
                val updatedUser = user.copy(
                    password = hashPassword,
                    firstlogin = false,
                    synchronised = true
                )

                executeApiCall(
                    apiCall = {
                        apiService.changePassword(user.id ?: 0, updatedUser)
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
