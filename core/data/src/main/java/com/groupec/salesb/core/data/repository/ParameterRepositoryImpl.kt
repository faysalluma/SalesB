package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toParameter
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ParameterRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    ParameterRepository {
    override fun saveParameters(): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.getParameter()
            if (response.isSuccessful) {
                response.body()?.parameter?.toParameter()?.let {
                    dataStoreManager.setParameterConfig(it)
                    emit(Result.Success(true))
                }
            } else {
                emit(Result.Error(HttpException(response)))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }.flowOn(Dispatchers.IO)

    override fun getParameters(): Flow<Parameter> = dataStoreManager.parameterFlow
}