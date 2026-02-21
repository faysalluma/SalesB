package com.groupec.salesb.core.domain.sale

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.sale.SaleRepository
import com.groupec.salesb.core.model.data.Sale
import javax.inject.Inject

class GetAllSalesUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(searchParams: Map<String, String>): Result<List<Sale>> {
        return saleRepository.getAllSales(searchParams)
    }
}
