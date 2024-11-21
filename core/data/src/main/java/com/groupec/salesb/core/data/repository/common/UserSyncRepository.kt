package com.groupec.salesb.core.data.repository.common


import android.content.Context
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.ConnectivityManagerUtils
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.Utility
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toUser
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.mindrot.jbcrypt.BCrypt
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserSyncRepository @Inject constructor(
    @ApplicationContext val context: Context,
    private val userLocalRepository: UserLocalRepository,
    private val userRemoteRepository: UserRemoteRepository,
) {
    suspend fun changePassword(userId: Int, ancPassword: String, password: String): Result<User> {
        val localResult = userLocalRepository.changePassword(userId, ancPassword, password)
        if (localResult is Result.Success) {
            if (Utility.checkForInternet(context)) {
                val remoteResult = userRemoteRepository.changePassword(userId, ancPassword, password)
                if (remoteResult is Result.Error) {
                    userLocalRepository.markUserForLaterSync(userId)
                }
            } else {
                userLocalRepository.markUserForLaterSync(userId)
            }
        }
        return localResult
    }
}