package com.groupec.salesb.core.domain.category

import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.model.data.Category
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetAllCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(searchQuery: String): Result<List<Category>> {
        return try {
            categoryRepository.getAllCategories(searchQuery)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}
