package com.groupec.salesb.core.data.repository.account

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toAuthenticatedUser
import com.groupec.salesb.core.data.model.toUserCompany
import com.groupec.salesb.core.data.model.toParameter
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserSession
import com.groupec.salesb.core.model.data.Login
import com.groupec.salesb.core.model.data.PasswordChange
import com.groupec.salesb.core.network.model.AccountResponse
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.R
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import retrofit2.Response
import java.util.Locale

class AccountRepositoryImpl(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager,
    private val context: Context
) : AccountRepository {
    private val mutex = Mutex()
    override val session = dataStoreManager.userSessionFlow

    override suspend fun login(email: String, password: String): Result<UserSession> = operation {
        val account = readAccount(apiService.login(Login(email.trim().lowercase(Locale.ROOT), password)))
        // A new login never inherits another account's company selection.
        saveSession(account, account.companies.singleOrNull()?.companyId)
    }

    override suspend fun changePassword(currentPassword: String, newPassword: String): Result<User> = operation {
        val previous = requireSession()
        val account = readAccount(apiService.changePassword(PasswordChange(
            email = requireNotNull(previous.account).email,
            currentPassword = currentPassword,
            newPassword = newPassword
        )))
        check(account.userId == requireNotNull(previous.account).userId) {
            context.getString(R.string.error_no_account)
        }
        saveSession(account, previous.selectedCompanyId)
        account
    }

    override suspend fun refreshCompanies(): Result<UserSession> = operation {
        val previous = requireSession()
        val account = refreshAccount(requireNotNull(previous.account))
        saveSession(account, previous.selectedCompanyId)
    }

    override suspend fun selectCompany(companyId: Int): Result<UserSession> = operation {
        val previous = requireSession()
        // Refresh active memberships before accepting a company from the UI/cache.
        val account = refreshAccount(requireNotNull(previous.account))
        if (account.companies.none { it.companyId == companyId }) {
            saveSession(account, previous.selectedCompanyId)
            throw IllegalStateException(context.getString(R.string.error_company_not_available))
        }
        saveSession(account, companyId)
    }

    override suspend fun logout(): Result<Unit> = operation {
        dataStoreManager.logout()
    }

    private suspend fun requireSession(): UserSession = session.first().also {
        if (it.account == null) throw IllegalStateException(context.getString(R.string.error_no_account))
    }

    private suspend fun refreshAccount(account: User): User {
        val body = when (val result = executeApiCall(context = context) { apiService.getCompanies(account.userId) }) {
            is Result.Success -> result.data
            is Result.Error -> throw result.exception
            is Result.Loading -> error(context.getString(R.string.error_empty_response))
        }
        return account.copy(companies = body.companies.map { it.toUserCompany() })
    }

    private suspend fun readAccount(response: Response<ApiResult<AccountResponse>>): User {
        if (response.isSuccessful && response.body() == null) {
            throw IllegalStateException(context.getString(R.string.error_empty_response))
        }
        val account = when (val result = executeApiCall(context = context) { response }) {
            is Result.Success -> {
                val body = result.data
                check(!body.error) {
                    body.message?.takeIf { it.isNotBlank() }
                        ?: context.getString(R.string.error_api_rejected)
                }
                checkNotNull(body.data) {
                    context.getString(R.string.error_empty_response)
                }.toAuthenticatedUser()
            }
            is Result.Error -> throw result.exception
            is Result.Loading -> error(context.getString(R.string.error_empty_response))
        }
        if (!account.active) throw IllegalStateException(context.getString(R.string.error_account_disabled))
        return account
    }

    private suspend fun saveSession(account: User, preferredCompanyId: Int?): UserSession {
        val selected = preferredCompanyId?.takeIf { id -> account.companies.any { it.companyId == id } }
            ?: account.companies.singleOrNull()?.companyId
        val updated = UserSession(account, selected)
        // Load the selected company's configuration before allowing entry to business screens.
        val parameter = selected?.let { companyId ->
            when (val result = executeApiCall(context = context) { apiService.getParameter(account.userId, companyId) }) {
                is Result.Success -> result.data.toParameter()
                is Result.Error -> throw result.exception
                is Result.Loading -> error(context.getString(R.string.error_empty_response))
            }
        }
        dataStoreManager.setUserSession(updated)
        parameter?.let { dataStoreManager.setParameterConfig(it) }
        return updated
    }

    private suspend fun <T> operation(block: suspend () -> T): Result<T> = mutex.withLock {
        try {
            Result.Success(block())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }
}
