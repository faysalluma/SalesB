package com.groupec.salesb.core.data.repository.category

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toCategorieList
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class CategoryPagingSource(
    private val api: ApiService,
    private val searchQuery: String
) : PagingSource<Int, Category>() {

    override fun getRefreshKey(state: PagingState<Int, Category>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Category> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getPagedCategories(currentPage, params.loadSize, searchQuery)
            if (response.isSuccessful) {
                val categories = response.body()?.toCategorieList().orEmpty()
                Log.d("Paging", "Loading page: $currentPage, items: ${categories.size}")

                LoadResult.Page(
                    data = categories,
                    prevKey = if (currentPage == 1) null else currentPage - 1, // null si pas de pagination vers le haut
                    nextKey = if (categories.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}