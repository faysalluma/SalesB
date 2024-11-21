package com.groupec.salesb.core.data.repository


import android.content.Context
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toUserEntity
import com.groupec.salesb.core.data.repository.common.UserLocalRepository
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.data.repository.common.UserSyncRepository
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import com.groupec.salesb.core.database.model.User as UserEntity

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
    private val userSyncRepository: UserSyncRepository
) : UserRepository {

    private suspend fun getOfflineMode() = dataStoreManager.parameterFlow.firstOrNull()?.offline

    /* Common methods */
    override fun saveDefaultUser(): Flow<Result<Unit>> = flow {
        try {
            val response = apiService.getDefaultUser()
            if (response.isSuccessful) {
                response.body()?.user?.toUserEntity()?.let {
                    emit(addUser(it))
                }
            } else {
                emit(Result.Error(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getUserStore(): Flow<UserStore> = dataStoreManager.userFlow

    private suspend fun addUser(user: UserEntity) = userLocalRepository.addUser(user)

    /* Sync methods */
    /* Get methods */
    override suspend fun checkLogin(email: String, password: String): Result<User> = if (getOfflineMode() == true) {
        userLocalRepository.checkLogin(email, password)
    } else {
        userRemoteRepository.checkLogin(email, password)
    }

    /* Set methods */
    override suspend fun changePassword(
        userId: Int,
        ancPassword: String,
        password: String
    ): Result<User> = if (getOfflineMode() == true) {
        userSyncRepository.changePassword(userId, ancPassword, password)
    } else {
        userRemoteRepository.changePassword(userId, ancPassword, password)
    }
}