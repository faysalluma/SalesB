package com.groupec.salesb.core.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.groupec.salesb.core.data.model.toProductList
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.network.retrofit.ApiService
import retrofit2.HttpException

class ProductPagingSource(
    private val api: ApiService,
    private val searchQuery: String
) : PagingSource<Int, Product>() {

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val currentPage = params.key ?: 1
            val response = api.getProducts(currentPage, params.loadSize, searchQuery)
            if (response.isSuccessful) {
                val products = response.body()?.toProductList().orEmpty()
                LoadResult.Page(
                    data = products,
                    prevKey = null,  // pour la pagination vers le haut,  if (currentPage == 1) null else currentPage - 1
                    nextKey = if (products.isEmpty()) null else currentPage + 1
                )
            } else {
                LoadResult.Error(HttpException(response))
            }
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}