package com.groupec.salesb.core.domain.parameter

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveParameterUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    operator fun invoke(): Flow<Result<String>> = parameterRepository.saveParameters()
}