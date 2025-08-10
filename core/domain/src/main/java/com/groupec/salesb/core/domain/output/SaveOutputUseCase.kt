package com.groupec.salesb.core.domain.category

import javax.inject.Inject
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.OutputRepository
import com.groupec.salesb.core.model.data.Output

class SaveOutputUseCase @Inject constructor(private val outputRepository: OutputRepository) {
    suspend operator fun invoke(output: Output) : Result<Unit> = outputRepository.saveOutput(output)
}