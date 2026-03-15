package com.groupec.salesb.core.domain.parameter

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import com.groupec.salesb.core.model.data.Parameter
import javax.inject.Inject

class SaveParameterUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    suspend operator fun invoke(parameter: Parameter): Result<Unit> = parameterRepository.saveParameters(parameter)
}
