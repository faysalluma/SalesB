package com.groupec.salesb.core.data.repository.user

import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Employee
import com.groupec.salesb.core.model.data.EmployeeInput
import com.groupec.salesb.core.model.data.UserStore
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getUserStore(): Flow<UserStore>
    suspend fun isSubscriptionExpired(): Boolean?
    suspend fun logout(): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun updateUserSubscriptionStatus(productId: String? = null, purchaseToken: String? = null): Result<Unit>
    fun getPagedUsers(searchQuery: String): Flow<PagingData<Employee>>
    suspend fun getAllUsers(searchQuery: String): Result<List<Employee>>
    suspend fun saveUser(user: EmployeeInput): Result<Unit>
    suspend fun deleteUser(memberId: Long): Result<Unit>
}
