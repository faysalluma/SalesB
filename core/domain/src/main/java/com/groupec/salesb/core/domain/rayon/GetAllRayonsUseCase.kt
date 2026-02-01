package com.groupec.salesb.core.domain.rayon

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import com.groupec.salesb.core.model.data.Rayon
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAllRayonsUseCase @Inject constructor(
    private val rayonRepository: RayonRepository
) {
    suspend operator fun invoke(searchQuery: String): Result<List<Rayon>> {
        return try {
            val rayons = rayonRepository.getRayons(searchQuery).first()
            Result.Success(rayons)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
