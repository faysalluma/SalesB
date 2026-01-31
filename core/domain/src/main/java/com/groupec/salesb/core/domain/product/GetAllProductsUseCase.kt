package com.groupec.salesb.core.domain.product

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.product.ProductRepository
import com.groupec.salesb.core.model.data.Product
import javax.inject.Inject

class GetAllProductsUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(searchQuery: String): Result<List<Product>> =  productRepository.getProducts(searchQuery)
}
