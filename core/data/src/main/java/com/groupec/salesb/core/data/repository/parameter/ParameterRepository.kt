package com.groupec.salesb.core.data.repository.parameter



import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.flow.Flow

interface ParameterRepository{
    suspend fun saveParameters(parameter: Parameter) : Result<Unit>
    fun getParameters() : Flow<Parameter>
    suspend fun updateFirstLogin(): Result<Unit>
    suspend fun acceptTermsAndConditions(): Result<Unit>
}
