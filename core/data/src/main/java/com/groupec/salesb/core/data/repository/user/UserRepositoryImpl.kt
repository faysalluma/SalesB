package com.groupec.salesb.core.data.repository.user


import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUserEntity
import com.groupec.salesb.core.data.model.toUserList
import com.groupec.salesb.core.data.repository.common.UserLocalRepository
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.data.repository.common.UserSyncRepository
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.getDateTimeByNtp
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.model.data.others.Subscription
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mindrot.jbcrypt.BCrypt
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
                response.body()?.toUserEntity()?.let {
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

    override fun getPagedUsers(searchQuery: String): Flow<PagingData<User>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                UserPagingSource(apiService, searchQuery)
            }
        ).flow
    }

    override suspend fun getAllUsers(searchQuery: String): Result<List<User>> {
        return try {
            val response = apiService.getUsers(searchQuery)
            if (!response.isSuccessful) {
                return Result.Error(HttpException(response))
            }
            val users = response.body()?.toUserList().orEmpty()
            Result.Success(users)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveUser(user: User): Result<Unit> {
        val hashPassword = BCrypt.hashpw(user.password, BCrypt.gensalt())
        val updatedUser = user.copy(password = hashPassword)
        return executeApiCall(context) {
            apiService.addUser(updatedUser)
        }
    }

    override suspend fun deleteUser(userId: Int): Result<Unit> {
        return safeApiCall(
            apiCall = { apiService.deleteUser(userId) },
            transform = {
                Result.Success(Unit)
            }
        )
    }

    /* Sync methods */
    /* Get methods */
    override suspend fun checkLogin(email: String, password: String): Result<Pair<User, Boolean>> {
        return if (getOfflineMode() == true) {
            userLocalRepository.checkLogin(email, password)
        } else {
            isExpired()?.let { isExpired ->
                if (!isExpired) {
                    userRemoteRepository.checkLogin(email, password)
                } else {
                    Result.Error(Exception(context.getString(R.string.subscription_expired)))
                }
            } ?: Result.Error(Exception(context.getString(R.string.error_subscription_expired)))
        }
    }

    override suspend fun isSubscriptionExpired(): Boolean? {
        val now = System.currentTimeMillis()
        val subscription = dataStoreManager.subscriptionFlow.first()
        val lastCheck = subscription.last_sub_check

        // Si la dernière vérification date de moins de 24h → renvoyer le statut stocké
        if (now - lastCheck < 24 * 60 * 60 * 1000L) {
            return subscription.last_sub_status
        }

        // Sinon faire un nouveau check
        val expired = isExpired()

        // Mettre à jour le cache seulement si on a une réponse valide
        if (expired != null) {
            dataStoreManager.setSubscriptionConfig(
                Subscription(
                    last_sub_check = now,
                    last_sub_status = expired
                )
            )
        }

        return expired
    }

    suspend fun isExpired(): Boolean? {
        return try {
            val response = apiService.getParameter()
            if (!response.isSuccessful) return null

            val expirationDate = response.body()?.parameter?.expirationdate
            expirationDate?.let { it < getDateTimeByNtp() } ?: true
        } catch (e: Exception) {
            null
        }
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

    override suspend fun logout(): Result<Unit> {
        return try {
            dataStoreManager.logout()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> {
        return executeApiCall (context = context) {
            apiService.forgotPassword(email)
        }
    }
}
