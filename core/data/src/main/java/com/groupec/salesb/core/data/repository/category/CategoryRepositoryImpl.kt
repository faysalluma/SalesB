package com.groupec.salesb.core.data.repository.category

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.model.toCategorieList
import com.groupec.salesb.core.data.repository.product.ProductPagingSource
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.executeApiCall
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    CategoryRepository {
    override fun getCategories(): Flow<List<Category>> = flow {
        val result = safeApiCall(
            apiCall = { apiService.getCategories() },
            transform = { response ->
                response.toCategorieList()
            }
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

    override fun getPagedCategories(searchQuery: String): Flow<PagingData<Category>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CategoryPagingSource(apiService, searchQuery)
            }
        ).flow
    }

    override suspend fun saveCategory(category: Category): Result<Unit> {
        val categoryValue = category.copy(
            userid = dataStoreManager.userFlow.first().id.toInt()
        )
        return executeApiCall(
            apiCall = {
                apiService.addCategory(categoryValue)
            }
        )
    }

    override suspend fun deleteCategory(categoryId: Int): Result<Unit> {
        val result  = safeApiCall(
            apiCall = { apiService.deleteCategory(categoryId) },
            transform = {
                Result.Success(Unit)
            }
        )
        return result
    }

}