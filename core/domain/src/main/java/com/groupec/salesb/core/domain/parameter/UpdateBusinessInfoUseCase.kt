package com.groupec.salesb.core.domain.parameter

import android.net.Uri
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.parameter.ParameterRepository
import com.groupec.salesb.core.model.data.Parameter
import javax.inject.Inject

class UpdateBusinessInfoUseCase @Inject constructor(
    private val parameterRepository: ParameterRepository
) {
    suspend operator fun invoke(
        parameter: Parameter,
        logoUri: Uri? = null,
        tva: String? = null
    ): Result<Parameter> {
        return parameterRepository.updateBusinessInfo(parameter, logoUri, tva)
    }
}
