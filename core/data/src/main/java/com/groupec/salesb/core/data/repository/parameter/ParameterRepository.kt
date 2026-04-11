package com.groupec.salesb.core.data.repository.parameter


import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Parameter
import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface ParameterRepository{
    suspend fun saveParameters(parameter: Parameter) : Result<Unit>
    suspend fun getParameterStore(): Result<Parameter>
    suspend fun updateBusinessInfo(
        parameter: Parameter,
        logoUri: Uri? = null,
        tva: String? = null
    ): Result<Parameter>
    fun getParameters() : Flow<Parameter>
    suspend fun updateFirstLogin(): Result<Unit>
    suspend fun acceptTermsAndConditions(): Result<Unit>
}
