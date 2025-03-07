package com.groupec.salesb.core.domain.parameter

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.ParameterRepository
import javax.inject.Inject

class UpdateFirstLoginParameterUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    suspend operator fun invoke(): Result<Unit> = parameterRepository.updateFirstLogin()
}