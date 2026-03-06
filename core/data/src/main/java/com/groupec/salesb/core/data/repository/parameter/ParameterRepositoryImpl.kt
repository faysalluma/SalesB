package com.groupec.salesb.core.data.repository.parameter

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParameterRepositoryImpl @Inject constructor(private val dataStoreManager: DataStoreManager) :
    ParameterRepository {
    override suspend fun saveParameters(parameter: Parameter): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.setParameterConfig(parameter)
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getParameters(): Flow<Parameter> = dataStoreManager.parameterFlow

    override suspend fun updateFirstLogin(): Result<Unit> {
        return Result.Success(Unit)
    }

    override suspend fun acceptTermsAndConditions(): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                dataStoreManager.acceptTermsAndConditions()
                Result.Success(Unit)
            }
        } catch (e: Exception){
            Result.Error(e)
        }
    }
}
