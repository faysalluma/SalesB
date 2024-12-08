package com.groupec.salesb.core.domain.statistic

import com.groupec.salesb.core.data.repository.StatisticRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalSaleDayUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(date: String) : Flow<Pair<Double, Double>> = statisticRepository.getTotalSaleMorningEvening(date)
}