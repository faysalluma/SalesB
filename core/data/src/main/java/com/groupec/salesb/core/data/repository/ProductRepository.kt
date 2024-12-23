package com.groupec.salesb.core.data.repository

import androidx.paging.PagingData
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getPagedProducts(searchQuery: String) : Flow<PagingData<Product>>
}