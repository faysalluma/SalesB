package com.groupec.salesb.core.data.repository.rayon

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Rayon
import kotlinx.coroutines.flow.Flow

interface RayonRepository {
    fun getRayons(searchQuery: String): Flow<List<Rayon>>
    suspend fun saveRayon(rayon: Rayon) : Result<Unit>
    suspend fun deleteRayon(rayonId: Int) : Result<Unit>
}