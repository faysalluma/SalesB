package com.groupec.salesb.core.data.repository.user

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow

interface UserRepository{
    fun saveDefaultUser() : Flow<Result<Unit>>
    suspend fun checkLogin(email: String, password: String) : Result<Pair<User, Boolean>>
    fun getUserStore() : Flow<UserStore>
    suspend fun changePassword(userId: Int, ancPassword: String, password: String) : Result<User>
    suspend fun logout() : Result<Unit>
    suspend fun forgotPassword(email: String) : Result<Unit>
}