package com.groupec.salesb.core.data.repository.handleservice

import android.content.Context
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.data.model.toParameter
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleServiceRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager,
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) : HandleServiceRepository {
    private val updateMutex = Mutex()

    override fun getHandleServiceParameters(): Flow<Parameter> = dataStoreManager.parameterFlow

    override suspend fun updateShowImageOnProduct(value: Boolean): Result<Unit> =
        updateOption("showImageOnProduct", value)

    override suspend fun updateUseIntForPriceAndAmount(value: Boolean): Result<Unit> =
        updateOption("useIntForPriceAndAmount", value)

    override suspend fun updateActivePaymentMode(value: Boolean): Result<Unit> =
        updateOption("activePaymentMode", value)

    override suspend fun updateActiveClient(value: Boolean): Result<Unit> =
        updateOption("activeClient", value)

    override suspend fun updateActivePrinter(value: Boolean): Result<Unit> =
        updateOption("activePrinter", value)

    override suspend fun updateCashEnabled(value: Boolean): Result<Unit> =
        updateOption("cash_enabled", value)

    private suspend fun updateOption(field: String, value: Boolean): Result<Unit> =
        withContext(Dispatchers.IO) {
            updateMutex.withLock {
                try {
                    val session = dataStoreManager.userSessionFlow.first()
                    val user = session.account?.takeIf { it.active }
                        ?: return@withLock Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_no_account)))
                    val company = session.selectedCompany
                        ?: return@withLock Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                    val parameter = dataStoreManager.parameterFlow.first()
                    if (parameter.userid != user.userId || parameter.entrepriseid != company.companyId) {
                        return@withLock Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                    }
                    val response =
                        apiService.updateParameter(
                            userid = user.userId, entrepriseid = company.companyId,
                            raisonsociale = null,
                            businessType = (if (parameter.isServiceBusiness) "SERVICE" else "COMMERCE").toRequestBody("text/plain".toMediaType()),
                            ifu = null, adresse = null, telephone = null, email = null, website = null,
                            devise = parameter.devise.toRequestBody("text/plain".toMediaType()),
                            tva = null, logoPart = null,
                            options = mapOf(field to (if (value) "1" else "0").toRequestBody("text/plain".toMediaType()))
                        )
                    if (field == "cash_enabled" && !value && response.code() == 409) {
                        return@withLock Result.Error(Exception(context.getString(R.string.error_close_cash_before_disabling)))
                    }
                    val result = executeApiCall(context = context) { response }
                    when (result) {
                        is Result.Success -> {
                            val body = result.data
                            if (body.error) {
                                Result.Error(Exception(body.message ?: context.getString(R.string.error_updating_data)))
                            } else {
                                val updated = body.data?.toParameter()
                                    ?: return@withLock Result.Error(Exception(context.getString(R.string.error_empty_response)))
                                if (updated.userid != user.userId || updated.entrepriseid != company.companyId) {
                                    return@withLock Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                                }
                                dataStoreManager.setParameterConfig(updated)
                                Result.Success(Unit)
                            }
                        }
                        is Result.Error -> {
                            if (result.exception is CancellationException) throw result.exception
                            result
                        }
                        is Result.Loading -> Result.Loading
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (exception: IllegalArgumentException) {
                    Result.Error(Exception(context.getString(com.groupec.salesb.core.network.R.string.error_company_not_available)))
                } catch (exception: Exception) {
                    Result.Error(exception)
                }
            }
        }
}
