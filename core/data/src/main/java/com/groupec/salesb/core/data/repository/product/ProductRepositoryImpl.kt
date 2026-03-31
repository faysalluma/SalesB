package com.groupec.salesb.core.data.repository.product

import android.content.Context
import android.net.Uri
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.groupec.salesb.core.UploadUtility.Companion.deleteDirectoryFromCache
import com.groupec.salesb.core.UploadUtility.Companion.deleteImageFromCache
import com.groupec.salesb.core.UploadUtility.Companion.getRealPathFromURI
import com.groupec.salesb.core.data.model.toProductList
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.network.retrofit.ApiService
import kotlinx.coroutines.flow.Flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.data.R
import com.groupec.salesb.core.datastore.DataStoreManager
import com.groupec.salesb.core.network.retrofit.common.safeApiCall
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: ApiService,
    private val dataStoreManager: DataStoreManager
) : ProductRepository {
    private suspend fun currentUserId(): Int =
        dataStoreManager.userFlow.firstOrNull()?.id?.toIntOrNull() ?: 0

    override fun getPagedProducts(searchQuery: String): Flow<PagingData<Product>> {
        return flow {
            val userId = currentUserId()
            emitAll(
                Pager(
                    config = PagingConfig(
                        pageSize = 15,
                        initialLoadSize = 15,
                        enablePlaceholders = false
                    ),
                    pagingSourceFactory = {
                        ProductPagingSource(apiService, searchQuery, userId)
                    }
                ).flow
            )
        }
    }

    override suspend fun getProducts(searchQuery: String): Result<List<Product>> {
        return try {
            val response = apiService.getProducts(searchQuery, currentUserId())
            if (!response.isSuccessful) {
                throw HttpException(response)
            }
            Result.Success(response.body()?.toProductList().orEmpty())
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }

    override suspend fun saveProduct(product: Product, uriImage: Uri?): Result<Unit> {
        val userId = currentUserId()
        // Image part
        val imagePart = uriImage?.let{
            // If not change image when update
            if (it.toString().substringAfterLast("/") == product.image) {
                 null // Don't upload again
            } else {
                prepareImageForUpload(context, it)
            }
        }
        if (imagePart != null) {
            deleteDirectoryFromCache(context)
        }

        // Required field
        val libelleBody = product.libelle.toRequestBody("text/plain".toMediaTypeOrNull())
        val prixttcBody = product.prixttc.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        val useridBody = userId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

        // Optional field
        val idBody = product.id?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val referenceBody = product.reference?.toRequestBody("text/plain".toMediaTypeOrNull())
        val descriptionBody = product.description?.toRequestBody("text/plain".toMediaTypeOrNull())
        val prixhtBody = product.prixht?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val qtestockBody =
            product.qtestock?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val stockminiBody =
            product.stockmini?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val categorieidBody =
            product.categorieid?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val rayonidBody =
            product.rayonid?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val fournisseuridBody =
            product.fournisseurid?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val tvaidBody = product.tvaid?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageBody = product.image?.toRequestBody("text/plain".toMediaTypeOrNull())


        val result = runCatching {
            val response =  apiService.addProduct(
                idBody, referenceBody, libelleBody, descriptionBody, imageBody, prixhtBody, prixttcBody,
                qtestockBody, stockminiBody, categorieidBody, rayonidBody, fournisseuridBody,
                tvaidBody, useridBody, imagePart
            )
            if (response.isSuccessful) {
                val body = response.body()
                    ?: return@runCatching Result.Error(Exception(context.getString(R.string.error_empty_response)))

                if (body.error) {
                    Result.Error(
                        Exception(
                            body.message?.takeIf { it.isNotBlank() }
                                ?: "Error when saving product"
                        )
                    )
                } else {
                    Result.Success(Unit)
                }
            } else {
                Result.Error(HttpException(response))
            }
        }.getOrElse {
            Result.Error(it)
        }
        // Delete file from caches
        deleteImageFromCache(context, uriImage?.lastPathSegment ?: "")
        return result
    }

    override suspend fun deleteProduct(productId: Int): Result<Unit> {
        val result  = safeApiCall(
            apiCall = { apiService.deleteProduct(productId) },
            transform = {
                Result.Success(Unit)
            }
        )
        return result
    }

    fun prepareImageForUpload(context: Context, imageUri: Uri): MultipartBody.Part? {
        val filePath = getRealPathFromURI(context, imageUri) ?: return null // Get file path
        val file = File(filePath)
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "imagePart", // Nom du champ attendu par l'API
            file.name,
            requestBody
        )
    }
}
