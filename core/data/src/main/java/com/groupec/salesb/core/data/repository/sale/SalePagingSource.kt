package com.groupec.salesb.core.data.repository.sale

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toSaleList
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class SalePagingSource(
    private val api: ApiService,
    private val searchParams: Map<String, String>
) : PagingSource<Int, Sale>() {

    override fun getRefreshKey(state: PagingState<Int, Sale>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Sale> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getSales(currentPage, params.loadSize, searchParams)
            if (response.isSuccessful) {
                val sales = response.body()?.toSaleList().orEmpty()
                Log.d("Paging", "Loading page: $currentPage, items: ${sales.size}")

                LoadResult.Page(
                    data = sales,
                    prevKey =  if (currentPage == 1) null else currentPage - 1,  // ou null si je ne veux pas naviguer vers le haut
                    nextKey = if (sales.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}