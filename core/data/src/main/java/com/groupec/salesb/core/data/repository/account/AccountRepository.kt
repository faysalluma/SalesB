package com.groupec.salesb.core.data.repository.account

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserSession
import kotlinx.coroutines.flow.Flow

interface AccountRepository {
    val session: Flow<UserSession>
    suspend fun login(email: String, password: String): Result<UserSession>
    suspend fun changePassword(currentPassword: String, newPassword: String): Result<User>
    suspend fun refreshCompanies(): Result<UserSession>
    suspend fun selectCompany(companyId: Int): Result<UserSession>
    suspend fun logout(): Result<Unit>
}

