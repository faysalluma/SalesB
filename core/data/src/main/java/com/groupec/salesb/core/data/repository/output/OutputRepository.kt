package com.groupec.salesb.core.data.repository.category

import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Output
import kotlinx.coroutines.flow.Flow

interface OutputRepository {
    fun getOutputs(searchQuery: String) : Flow<PagingData<Output>>
    suspend fun getAllOutputs(searchQuery: String) : Result<List<Output>>
    suspend fun saveOutput(output: Output) : Result<Unit>
    suspend fun deleteOutput(outputId: Int) : Result<Unit>
}
