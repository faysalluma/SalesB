package com.groupec.salesb.core.data.repository.user

import android.content.Context
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toEmployee
import com.groupec.salesb.core.data.repository.common.UserRemoteRepository
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Employee
import com.groupec.salesb.core.model.data.EmployeeInput
import com.groupec.salesb.core.model.data.UserSession
import com.groupec.salesb.core.model.data.UserStore
import com.groupec.salesb.core.network.R
import com.groupec.salesb.core.network.model.SubscriptionStatusRequest
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import java.util.Locale

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val userRemoteRepository: UserRemoteRepository,
    private val context: Context
) : UserRepository {
    override fun getUserStore(): Flow<UserStore> = dataStoreManager.userFlow

    private suspend fun selectedSession(): UserSession = dataStoreManager.userSessionFlow.first().also {
        if (it.account == null) throw IllegalStateException(context.getString(R.string.error_no_account))
        if (it.selectedCompany == null) throw IllegalStateException(context.getString(R.string.error_company_not_available))
    }

    override fun getPagedUsers(searchQuery: String): Flow<PagingData<Employee>> = flow {
        val session = selectedSession()
        val userId = requireNotNull(session.account).userId
        val companyId = requireNotNull(session.selectedCompany).companyId
        emitAll(Pager(PagingConfig(pageSize = 15, initialLoadSize = 15, enablePlaceholders = false)) {
            UserPagingSource(apiService, userId, companyId, searchQuery)
        }.flow)
    }

    override suspend fun getAllUsers(searchQuery: String): Result<List<Employee>> = operation {
        val session = selectedSession()
        val users = mutableListOf<Employee>()
        var page = 1
        do {
            val response = safeApiCall({ apiService.getPagedUsers(
                requireNotNull(session.account).userId, requireNotNull(session.selectedCompany).companyId,
                page, 100, searchQuery
            ) }) { it }
            users.addAll(response.members.map { it.toEmployee() })
            page++
        } while (response.members.size == 100)
        users
    }

    override suspend fun saveUser(user: EmployeeInput): Result<Unit> = operation {
        val session = selectedSession()
        val actor = requireNotNull(session.account).userId
        val company = requireNotNull(session.selectedCompany).companyId
        val input = user.copy(email = user.email.trim().lowercase(Locale.ROOT))
        safeApiCall({
            if (input.memberId == null) apiService.addUser(actor, company, input)
            else apiService.updateUser(actor, company, requireNotNull(input.memberId), input)
        }) { check(!it.error && it.data != null) }
    }

    override suspend fun deleteUser(memberId: Long): Result<Unit> = operation {
        val session = selectedSession()
        safeApiCall({ apiService.deleteUser(
            requireNotNull(session.account).userId, requireNotNull(session.selectedCompany).companyId, memberId
        ) }) { check(!it.error && it.data != null) }
    }

    override suspend fun isSubscriptionExpired(): Boolean? {
        val expired = userRemoteRepository.checkIfSubscriptionExpired()
        if (expired != null) dataStoreManager.saveUserSubscriptionStatus(!expired)
        return expired
    }

    override suspend fun logout(): Result<Unit> = operation { dataStoreManager.logout() }

    override suspend fun forgotPassword(email: String): Result<Unit> = operation {
        safeApiCall({ apiService.forgotPassword(email.trim().lowercase(Locale.ROOT)) }) { Unit }
    }

    override suspend fun updateUserSubscriptionStatus(productId: String?, purchaseToken: String?): Result<Unit> = operation {
        val session = selectedSession()
        require(!productId.isNullOrBlank() && !purchaseToken.isNullOrBlank())
        val result = safeApiCall({ apiService.updateUserSubscriptionStatus(
            requireNotNull(session.account).userId, requireNotNull(session.selectedCompany).companyId,
            SubscriptionStatusRequest(productId, purchaseToken)
        ) }) { it }
        check(!result.error)
        dataStoreManager.saveUserSubscriptionStatus(requireNotNull(result.data).isEntitled == 1)
    }

    private suspend fun <T> operation(block: suspend () -> T): Result<T> = try {
        Result.Success(block())
    } catch (exception: CancellationException) {
        throw exception
    } catch (exception: Exception) {
        Result.Error(exception)
    }
}
