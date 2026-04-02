package com.groupec.salesb.core.data.repository.client

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toClientList
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class ClientPagingSource(
    private val api: ApiService,
    private val searchQuery: String,
    private val userId: Int
) : PagingSource<Int, Client>() {

    override fun getRefreshKey(state: PagingState<Int, Client>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Client> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getPagedClients(currentPage, params.loadSize, searchQuery, userId)
            if (response.isSuccessful) {
                val clients = response.body()?.toClientList().orEmpty()
                LoadResult.Page(
                    data = clients,
                    prevKey = if (currentPage == 1) null else currentPage - 1,
                    nextKey = if (clients.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}
