package com.groupec.salesb.core.data.repository.user

import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow

interface UserRepository{
    fun saveDefaultUser() : Flow<Result<Unit>>
    suspend fun checkEmailExists(email: String): Result<Boolean>
    suspend fun saveUserStoreById(id: Int): Result<Unit>
    suspend fun checkLogin(email: String, password: String) : Result<Pair<User, Boolean>>
    suspend fun isSubscriptionExpired(): Boolean?
    fun getUserStore() : Flow<UserStore>
    suspend fun changePassword(userId: Int, ancPassword: String, password: String) : Result<User>
    suspend fun logout() : Result<Unit>
    suspend fun forgotPassword(email: String) : Result<Unit>
    suspend fun updateUserSubscriptionStatus(
        productId: String? = null,
        purchaseToken: String? = null
    ) : Result<Unit>
    fun getPagedUsers(searchQuery: String) : Flow<PagingData<User>>
    suspend fun getAllUsers(searchQuery: String) : Result<List<User>>
    suspend fun saveUser(user: User) : Result<Unit>
    suspend fun deleteUser(userId: Int) : Result<Unit>
}
