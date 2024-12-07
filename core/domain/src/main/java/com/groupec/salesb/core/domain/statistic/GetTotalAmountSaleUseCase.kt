package com.groupec.salesb.core.domain.statistic

import com.groupec.salesb.core.data.repository.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalAmountSaleUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(startDate: String, endDate: String) : Flow<Int> = statisticRepository.getTotalAmountSales(startDate, endDate)
}