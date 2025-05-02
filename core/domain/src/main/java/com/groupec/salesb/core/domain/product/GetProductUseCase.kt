package com.groupec.salesb.core.domain.product

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.product.ProductRepository
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    operator fun invoke(searchQuery : String) : Flow<PagingData<Product>> = productRepository.getPagedProducts(searchQuery)
}