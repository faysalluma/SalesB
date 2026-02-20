package com.groupec.salesb.core.data.repository.user

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toUserList
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class UserPagingSource(
    private val api: ApiService,
    private val searchQuery: String
) : PagingSource<Int, User>() {

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getPagedUsers(currentPage, params.loadSize, searchQuery)
            if (response.isSuccessful) {
                val users = response.body()?.toUserList().orEmpty()
                Log.d("Paging", "Loading page: $currentPage, items: ${users.size}")

                LoadResult.Page(
                    data = users,
                    prevKey = if (currentPage == 1) null else currentPage - 1, // null si pas de pagination vers le haut
                    nextKey = if (users.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}