package com.groupec.salesb.core.domain.product

import com.groupec.salesb.core.data.repository.product.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalProductUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke() : Flow<Int> = statisticRepository.getTotalProducts()
}