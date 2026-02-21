package com.groupec.salesb.core.domain.sale

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.sale.SaleRepository
import com.groupec.salesb.core.model.data.Sale
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSaleUseCase @Inject constructor(private val saleRepository: SaleRepository) {
    operator fun invoke(searchParams : Map<String, String>) : Flow<PagingData<Sale>> = saleRepository.getPagedSales(searchParams)
}
