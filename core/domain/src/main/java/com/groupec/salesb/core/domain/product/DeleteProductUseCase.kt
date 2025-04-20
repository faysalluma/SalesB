package com.groupec.salesb.core.domain.product

import com.groupec.salesb.core.data.repository.product.ProductRepository
import javax.inject.Inject
import com.groupec.salesb.core.Result

class DeleteProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(productId: Int) : Result<Unit> = productRepository.deleteProduct(productId)
}