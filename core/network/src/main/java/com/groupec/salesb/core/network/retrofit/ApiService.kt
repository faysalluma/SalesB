package com.groupec.salesb.core.network.retrofit

import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.model.CategoryResponse
import com.groupec.salesb.core.network.model.ChartDateResponse
import com.groupec.salesb.core.network.model.ChartDayResponse
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.model.ProductResponse
import com.groupec.salesb.core.network.model.RayonResponse
import com.groupec.salesb.core.network.model.SaleResponse
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserResponse
import com.groupec.salesb.core.network.retrofit.common.Constants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    /* GET API */
    @GET(Constants.GET_PARAMETER)
    suspend fun getParameter() : Response<ParameterResponse>

    @GET(Constants.GET_DEFAULT_USER)
    suspend fun getDefaultUser() : Response<UserResponse>

    @GET(Constants.GET_USER_BY_EMAIL)
    suspend fun getUserByEmail(@Path("email") email: String) : Response<ApiResult<UserItemResponse>>

    @GET(Constants.GET_USER_BY_ID)
    suspend fun getUserById(@Path("userid") userId: Int) : Response<ApiResult<UserItemResponse>>

    @GET(Constants.GET_TOTAL_SALES)
    suspend fun getTotalSales(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Response<ApiResult<Int>>

    @GET(Constants.GET_TOTAL_AMOUNT_SALES)
    suspend fun getTotalAmountSales(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Response<ApiResult<Double>>

    @GET(Constants.GET_TOTAL_PRODUCTS)
    suspend fun getTotalProducts(): Response<ApiResult<Int>>

    @GET(Constants.GET_TOP_SALE_PRODUCTS)
    suspend fun getTopSaleProducts(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Response<ProductResponse>

    @GET(Constants.GET_ALERT_SEUIL)
    suspend fun getAlertSeuil(): Response<ApiResult<Int>>

    @GET(Constants.GET_TOTAL_SALE_DAY)
    suspend fun getTotalSaleMorningEvening(@Path("date") date: String): Response<ApiResult<ChartDayResponse>>

    @GET(Constants.GET_TOTAL_SALE_BY_DATE)
    suspend fun getTotalSalesByDate(@Path("startDate") startDate: String, @Path("endDate") endDate: String
    ): Response<ChartDateResponse>

    @GET(Constants.GET_PRODUCTS)
    suspend fun getProducts(@Query("page") page: Int, @Query("limit") limit: Int, @Query("search") search: String
    ): Response<ProductResponse>

    @GET(Constants.GET_PAGED_CATEGORIES)
    suspend fun getPagedCategories(@Query("page") page: Int, @Query("limit") limit: Int, @Query("search") search: String
    ): Response<CategoryResponse>

    @GET(Constants.GET_CATEGORIES)
    suspend fun getCategories(): Response<CategoryResponse>

    @GET(Constants.GET_RAYONS)
    suspend fun getRayons(@Query("search") search: String): Response<RayonResponse>

    @GET(Constants.GET_SALES)
    suspend fun getSales(@Query("page") page: Int, @Query("limit") limit: Int, @QueryMap searchParams: Map<String, String>
    ): Response<SaleResponse>

    /* POST API */
    @Multipart
    @POST(Constants.ADD_PRODUCT)
    suspend fun addProduct(
        @Part("id") id: RequestBody?,
        @Part("reference") reference: RequestBody?,
        @Part("libelle") libelle: RequestBody,
        @Part("description") description: RequestBody?,
        @Part("image") image: RequestBody?,
        @Part("prixht") prixht: RequestBody?,
        @Part("prixttc") prixttc: RequestBody,
        @Part("qtestock") qtestock: RequestBody?,
        @Part("stockmini") stockmini: RequestBody?,
        @Part("categorieid") categorieid: RequestBody?,
        @Part("rayonid") rayonid: RequestBody?,
        @Part("fournisseurid") fournisseurid: RequestBody?,
        @Part("tvaid") tvaid: RequestBody?,
        @Part("userid") userid: RequestBody,
        @Part imagePart: MultipartBody.Part?
    ): Response<ApiResult<Unit>>

    @POST(Constants.ADD_SALE)
    suspend fun addSale(@Body sale: Sale): Response<Unit>

    @POST(Constants.ADD_CATEGORY)
    suspend fun addCategory(@Body category: Category): Response<Unit>

    @POST(Constants.ADD_RAYON)
    suspend fun addRayon(@Body rayon: Rayon): Response<Unit>

    @POST(Constants.FORGOT_PASSWORD)
    suspend fun forgotPassword(@Path("email") email: String): Response<Unit>

    /* PUT API */
    @PUT(Constants.PUT_CHANGE_PASSWORD)
    suspend fun changePassword(@Path("userid") userid: Int, @Body user: User): Response<User>

    /* DELETE API */
    @DELETE(Constants.DELETE_PRODUCT)
    suspend fun deleteProduct(@Path("productid") productid: Int): Response<ApiResult<Boolean>>

    @DELETE(Constants.DELETE_CATEGORY)
    suspend fun deleteCategory(@Path("categoryid") categoryid: Int): Response<ApiResult<Boolean>>

    @DELETE(Constants.DELETE_RAYON)
    suspend fun deleteRayon(@Path("rayonid") rayonid: Int): Response<ApiResult<Boolean>>
}
