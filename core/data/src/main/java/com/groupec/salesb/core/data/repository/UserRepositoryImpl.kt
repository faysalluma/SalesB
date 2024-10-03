package com.groupec.salesb.core.data.repository


import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.data.model.toUserEntity
import com.groupec.salesb.core.database.room.dao.UserDao
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.mindrot.jbcrypt.BCrypt
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton
import com.groupec.salesb.core.database.model.User as UserEntity

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
    private val dataStoreManager: DataStoreManager
) : UserRepository {
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

    suspend fun addUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.insertOrUpdate(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun checkLogin(email: String, password: String): Result<User> {
        return try {
            val userEntity= userDao.getUserByEmail(email)
            if (userEntity == null) {
                Result.Error(Exception("User not found"))
            } else {
                if (BCrypt.checkpw(password, userEntity.password)) {
                    Result.Success(userEntity.toUser())
                } else {
                    Result.Error(Exception("Invalid password"))
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getUserStore(): Flow<UserStore> = dataStoreManager.userFlow
}