package com.groupec.salesb.core.data.repository.common

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.data.model.toUserStore
import com.groupec.salesb.core.database.room.dao.UserDao
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.fixBCryptHash
import com.groupec.salesb.core.model.data.User
import dagger.hilt.android.qualifiers.ApplicationContext
import org.mindrot.jbcrypt.BCrypt
import javax.inject.Inject
import javax.inject.Singleton
import com.groupec.salesb.core.database.model.User as UserEntity

@Singleton
class UserLocalRepository @Inject constructor(
    private val userDao: UserDao,
    private val dataStoreManager: DataStoreManager,
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

    suspend fun checkLogin(email: String, password: String): Result<Pair<User, Boolean>> {
        return try {
            val userEntity= userDao.getUserByEmail(email)
            if (userEntity == null) {
                Result.Error(Exception(context.getString(R.string.error_user_not_found)))
            } else {
                if (!userEntity.actif) {
                    Result.Error(Exception(context.getString(R.string.error_user_not_active)))
                } else {
                    val isMainPasswordValid =  BCrypt.checkpw(password, userEntity.password)
                    val isResetPasswordValid =  userEntity.reset_password?.let {
                        BCrypt.checkpw(password, it.fixBCryptHash())
                    } ?: false
                    val isResetPasswordExpired = userEntity.reset_expires?.let { it <= currentDateString() } ?: false
                    if (isMainPasswordValid || (isResetPasswordValid && !isResetPasswordExpired)) {
                        dataStoreManager.setUserConfig(userEntity.toUserStore())
                        Result.Success(userEntity.toUser() to isMainPasswordValid)
                    } else if (isResetPasswordValid) {
                        Result.Error(Exception(context.getString(R.string.error_tempory_password_expire)))
                    } else {
                        Result.Error(Exception(context.getString(R.string.error_invalid_password)))
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun changePassword(userId: Int, ancPassword: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserById(userId)
            when {
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
                    val rowsUpdated = userDao.changePassword(updatedUser)
                    if (rowsUpdated > 0) {
                        Result.Success(updatedUser.toUser())
                    } else {
                        Result.Error(Exception(context.getString(R.string.error_updating_data)))
                    }
                }
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun markUserForLaterSync(userId: Int) {
        try {
            userDao.markUserForLaterSync(userId)
        } catch (e: Exception) {
            println("Error marking user for later sync: ${e.message}")
        }
    }
}