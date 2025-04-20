package com.groupec.salesb.core.domain.sale

import com.groupec.salesb.core.data.repository.product.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalSaleUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(startDate: String, endDate: String) : Flow<Int> = statisticRepository.getTotalSales(startDate, endDate)
}