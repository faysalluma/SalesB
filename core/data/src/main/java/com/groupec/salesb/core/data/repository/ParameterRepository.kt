package com.groupec.salesb.core.data.repository



import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.flow.Flow

interface ParameterRepository{
    fun saveParameters() : Flow<Result<Boolean>>
    fun getParameters() : Flow<Parameter>
}