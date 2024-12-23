package com.groupec.feature.productdetail

import androidx.lifecycle.ViewModel
import com.groupec.salesb.core.domain.product.GetProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase
) : ViewModel()
