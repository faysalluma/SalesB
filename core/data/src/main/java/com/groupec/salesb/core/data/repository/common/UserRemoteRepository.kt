package com.groupec.salesb.core.data.repository.common

import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRemoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) {
    suspend fun checkIfSubscriptionExpired(): Boolean? {
        val session = dataStoreManager.userSessionFlow.first()
        val user = session.account ?: return null
        val company = session.selectedCompany ?: return null
        return try {
            safeApiCall({ apiService.getCompanySubscription(user.userId, company.companyId) }) {
                it.subscription.isEntitled != 1
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            null
        }
    }
}
