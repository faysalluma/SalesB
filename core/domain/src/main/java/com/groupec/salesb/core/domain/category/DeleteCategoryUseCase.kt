package com.groupec.salesb.core.domain.category

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(categoryId: Int): Result<Unit> = categoryRepository.deleteCategory(categoryId)
}