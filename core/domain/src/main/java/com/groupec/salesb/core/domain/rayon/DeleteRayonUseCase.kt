package com.groupec.salesb.core.domain.rayon

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import javax.inject.Inject

class DeleteRayonUseCase @Inject constructor(private val rayonRepository: RayonRepository) {
    suspend operator fun invoke(rayonId: Int): Result<Unit> = rayonRepository.deleteRayon(rayonId)
}