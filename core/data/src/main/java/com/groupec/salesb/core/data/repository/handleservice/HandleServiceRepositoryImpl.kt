package com.groupec.salesb.core.data.repository.handleservice

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HandleServiceRepositoryImpl @Inject constructor(
    private val dataStoreManager: DataStoreManager
) : HandleServiceRepository {

    override fun getHandleServiceParameters(): Flow<Parameter> = dataStoreManager.parameterFlow

    override suspend fun updateServiceView(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateServiceView(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateShowImageOnProduct(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateShowImageOnProduct(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateUseIntForPriceAndAmount(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateUseIntForPriceAndAmount(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateActivePaymentMode(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateActivePaymentMode(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateActiveClient(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateActiveClient(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updateActivePrinter(value: Boolean): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.updateActivePrinter(value)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
