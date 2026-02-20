package com.groupec.salesb.core.domain.output

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.OutputRepository
import com.groupec.salesb.core.model.data.Output
import javax.inject.Inject

class GetAllOutputsUseCase @Inject constructor(
    private val outputRepository: OutputRepository
) {
    suspend operator fun invoke(searchQuery: String): Result<List<Output>> {
        return outputRepository.getAllOutputs(searchQuery)
    }
}
