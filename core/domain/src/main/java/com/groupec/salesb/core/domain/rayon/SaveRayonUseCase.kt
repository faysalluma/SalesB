package com.groupec.salesb.core.domain.rayon

import javax.inject.Inject
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import com.groupec.salesb.core.model.data.Rayon

class SaveRayonUseCase @Inject constructor(private val rayonRepository: RayonRepository) {
    suspend operator fun invoke(rayon: Rayon) : Result<Unit> = rayonRepository.saveRayon(rayon)
}