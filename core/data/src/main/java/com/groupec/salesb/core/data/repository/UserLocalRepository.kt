package com.groupec.salesb.core.data.repository


import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.database.room.dao.UserDao
import com.groupec.salesb.core.model.data.User
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton
import com.groupec.salesb.core.database.model.User as UserEntity

@Singleton
class UserLocalRepository @Inject constructor(
    private val userDao: UserDao,
    @ApplicationContext val context: Context
) {

    suspend fun addUser(user: UserEntity): Result<Unit> {
        return try {
            userDao.insertOrUpdate(user)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun checkLogin(email: String, password: String): Result<User> {
        return try {
            val userEntity= userDao.getUserByEmail(email)
            if (userEntity == null) {
                Result.Error(Exception(context.getString(R.string.error_user_not_found)))
            } else {
                if (BCrypt.checkpw(password, userEntity.password)) {
                    Result.Success(userEntity.toUser())
                } else {
                    Result.Error(Exception(context.getString(R.string.error_invalid_password)))
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}