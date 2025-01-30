package com.groupec.salesb.core.domain.product

import android.net.Uri

import com.groupec.salesb.core.data.repository.ProductRepository
import com.groupec.salesb.core.model.data.Product
import javax.inject.Inject
import com.groupec.salesb.core.Result

class SaveProductUseCase @Inject constructor(private val productRepository: ProductRepository) {
    suspend operator fun invoke(product: Product, uriImage : Uri?) : Result<Unit> = productRepository.saveProduct(product, uriImage)
}