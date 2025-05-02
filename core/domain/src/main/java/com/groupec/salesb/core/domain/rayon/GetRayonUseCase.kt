package com.groupec.salesb.core.domain.rayon

import com.groupec.salesb.core.data.repository.rayon.RayonRepository
import com.groupec.salesb.core.model.data.Rayon
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRayonUseCase @Inject constructor(private val rayonRepository: RayonRepository) {
    operator fun invoke(searchQuery : String) : Flow<List<Rayon>> = rayonRepository.getRayons(searchQuery)
}