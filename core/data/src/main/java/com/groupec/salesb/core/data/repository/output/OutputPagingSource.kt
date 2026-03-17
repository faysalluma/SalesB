package com.groupec.salesb.core.data.repository.category

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toOutputList
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class OutputPagingSource(
    private val api: ApiService,
    private val searchQuery: String,
    private val userId: Int
) : PagingSource<Int, Output>() {

    override fun getRefreshKey(state: PagingState<Int, Output>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Output> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getPagedOutputs(currentPage, params.loadSize, searchQuery, userId)
            if (response.isSuccessful) {
                val outputs = response.body()?.toOutputList().orEmpty()
                Log.d("Paging", "Loading page: $currentPage, items: ${outputs.size}")

                LoadResult.Page(
                    data = outputs,
                    prevKey = null,  // pour la pagination vers le haut,  if (currentPage == 1) null else currentPage - 1
                    nextKey = if (outputs.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
