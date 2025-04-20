package com.groupec.salesb.core.data.repository.product

import android.net.Uri
import androidx.paging.PagingData
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow
import com.groupec.salesb.core.Result

interface ProductRepository {
    fun getPagedProducts(searchQuery: String) : Flow<PagingData<Product>>
    suspend fun saveProduct(product: Product, uriImage : Uri?) : Result<Unit>
    suspend fun deleteProduct(productId: Int) : Result<Unit>
}