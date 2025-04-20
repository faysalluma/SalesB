package com.groupec.salesb.core.domain.product

import com.groupec.salesb.core.data.repository.product.StatisticRepository
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopSaleProductsUseCase @Inject constructor(private val statisticRepository: StatisticRepository) {
    operator fun invoke(startDate: String, endDate: String) : Flow<List<Product>> = statisticRepository.getTopSaleProducts(startDate, endDate)
}