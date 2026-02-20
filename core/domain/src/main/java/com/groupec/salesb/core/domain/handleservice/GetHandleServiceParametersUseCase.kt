package com.groupec.salesb.core.domain.handleservice

import com.groupec.salesb.core.data.repository.handleservice.HandleServiceRepository
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHandleServiceParametersUseCase @Inject constructor(
    private val handleServiceRepository: HandleServiceRepository
) {
    operator fun invoke(): Flow<Parameter> = handleServiceRepository.getHandleServiceParameters()
}
