package com.groupec.salesb.core.domain.statistic

import com.groupec.salesb.core.data.repository.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalSaleByDateUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(startDate: String, endDate: String) : Flow<List<Pair<String, Double>>> = statisticRepository.getTotalSalesByDate(startDate, endDate)
}