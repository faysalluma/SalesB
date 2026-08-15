package com.groupec.salesb.core.data.repository.handleservice

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Parameter
import kotlinx.coroutines.flow.Flow

interface HandleServiceRepository {
    fun getHandleServiceParameters(): Flow<Parameter>
    suspend fun updateShowImageOnProduct(value: Boolean): Result<Unit>
    suspend fun updateUseIntForPriceAndAmount(value: Boolean): Result<Unit>
    suspend fun updateActivePaymentMode(value: Boolean): Result<Unit>
    suspend fun updateActiveClient(value: Boolean): Result<Unit>
    suspend fun updateActivePrinter(value: Boolean): Result<Unit>
}
