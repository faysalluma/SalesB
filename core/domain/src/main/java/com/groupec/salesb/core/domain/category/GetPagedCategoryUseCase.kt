package com.groupec.salesb.core.domain.category

import androidx.paging.PagingData
import com.groupec.salesb.core.data.repository.category.CategoryRepository
import com.groupec.salesb.core.model.data.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPagedCategoryUsecase @Inject constructor(private val categoryRepository: CategoryRepository) {
    operator fun invoke(searchQuery : String) : Flow<PagingData<Category>> = categoryRepository.getPagedCategories(searchQuery)
}