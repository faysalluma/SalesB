package com.groupec.salesb.core.domain

import com.groupec.salesb.core.data.repository.ParameterRepository
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetParameterUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    operator fun invoke(): Flow<Parameter> = parameterRepository.getParameters()
}