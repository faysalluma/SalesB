package com.groupec.salesb.core.domain.parameter

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import javax.inject.Inject

class AcceptTermsAndConditionsUseCase @Inject constructor(private val parameterRepository: ParameterRepository) {
    suspend operator fun invoke(): Result<Unit> = parameterRepository.acceptTermsAndConditions()
}