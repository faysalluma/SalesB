package com.groupec.salesb.core.domain.category

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.OutputRepository
import javax.inject.Inject

class DeleteOutputUseCase @Inject constructor(private val outputRepository: OutputRepository) {
    suspend operator fun invoke(outputId: Int): Result<Unit> = outputRepository.deleteOutput(outputId)
}