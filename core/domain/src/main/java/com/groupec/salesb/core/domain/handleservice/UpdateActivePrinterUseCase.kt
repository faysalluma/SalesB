package com.groupec.salesb.core.domain.handleservice

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.handleservice.HandleServiceRepository
import javax.inject.Inject

class UpdateActivePrinterUseCase @Inject constructor(
    private val handleServiceRepository: HandleServiceRepository
) {
    suspend operator fun invoke(value: Boolean): Result<Unit> =
        handleServiceRepository.updateActivePrinter(value)
}
