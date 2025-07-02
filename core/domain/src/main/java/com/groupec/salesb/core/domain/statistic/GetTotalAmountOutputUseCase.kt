package com.groupec.salesb.core.domain.statistic

import com.groupec.salesb.core.data.repository.product.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalAmountOutputUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(startDate: String, endDate: String) : Flow<Double> = statisticRepository.getTotalAmountOutputs(startDate, endDate)
}