package com.groupec.salesb.core.domain.category

import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.model.data.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    operator fun invoke() : Flow<List<Category>> = categoryRepository.getCategories()
}