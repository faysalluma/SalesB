package com.groupec.salesb.core.domain.category

import javax.inject.Inject
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.model.data.Category

class SaveCategoryUseCase @Inject constructor(private val categoryRepository: CategoryRepository) {
    suspend operator fun invoke(category: Category) : Result<Unit> = categoryRepository.saveCategory(category)
}