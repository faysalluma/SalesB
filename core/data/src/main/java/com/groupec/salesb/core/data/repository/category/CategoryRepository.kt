package com.groupec.salesb.core.data.repository.category


import android.net.Uri
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<List<Category>>
    fun getPagedCategories(searchQuery: String) : Flow<PagingData<Category>>
    suspend fun saveCategory(category: Category) : Result<Unit>
    suspend fun deleteCategory(categoryId: Int) : Result<Unit>
}