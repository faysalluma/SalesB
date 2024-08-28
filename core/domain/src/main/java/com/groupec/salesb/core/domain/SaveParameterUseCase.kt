package com.groupec.salesb.core.domain

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.ParameterRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveParameterUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    operator fun invoke(): Flow<Result<String>> = parameterRepository.saveParameters()
}