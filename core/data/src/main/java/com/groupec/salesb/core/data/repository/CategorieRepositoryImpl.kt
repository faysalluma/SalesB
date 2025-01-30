package com.groupec.salesb.core.data.repository

import com.groupec.salesb.core.data.model.toCategorieList
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.model.data.Categorie
import com.groupec.salesb.core.network.retrofit.ApiService
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategorieRepositoryImpl @Inject constructor(private val apiService: ApiService, private val dataStoreManager: DataStoreManager) :
    CategorieRepository {
    override fun getCategories(): Flow<List<Categorie>> = flow {
        val result = safeApiCall(
            apiCall = { apiService.getCategories() },
            transform = { response ->
                response.toCategorieList()
            }
        )
        emit(result)
    }.flowOn(Dispatchers.IO)

}