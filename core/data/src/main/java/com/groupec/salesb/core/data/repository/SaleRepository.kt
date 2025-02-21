package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Sale

interface SaleRepository {
    suspend fun saveSale(sale: Sale) : Result<Unit>
}