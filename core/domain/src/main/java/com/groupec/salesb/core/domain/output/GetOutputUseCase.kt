package com.groupec.salesb.core.domain.output

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.category.OutputRepository
import com.groupec.salesb.core.model.data.Output
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetOutputUseCase @Inject constructor(private val outputRepository: OutputRepository) {
    operator fun invoke(searchQuery : String) : Flow<PagingData<Output>> = outputRepository.getOutputs(searchQuery)
}