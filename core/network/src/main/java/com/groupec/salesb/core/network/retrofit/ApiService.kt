package com.groupec.salesb.core.network.retrofit

import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.network.model.ApiResult
import com.groupec.salesb.core.network.model.ChartDateResponse
import com.groupec.salesb.core.network.model.ChartDayResponse
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.model.ProductResponse
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserResponse
import com.groupec.salesb.core.network.retrofit.common.Constants
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET(Constants.GET_PARAMETER)
    suspend fun getParameter() : Response<ParameterResponse>

    @GET(Constants.GET_DEFAULT_USER)
    suspend fun getDefaultUser() : Response<UserResponse>

    @GET(Constants.GET_USER_BY_EMAIL)
    suspend fun getUserByEmail(@Path("email") email: String) : Response<ApiResult<UserItemResponse>>

    @GET(Constants.GET_USER_BY_ID)
    suspend fun getUserById(@Path("userid") userId: Int) : Response<ApiResult<UserItemResponse>>

    @PUT(Constants.PUT_CHANGE_PASSWORD)
    suspend fun changePassword(@Path("userid") userid: Int, @Body user: User): Response<User>

    @GET(Constants.GET_TOTAL_SALES)
    suspend fun getTotalSales(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Response<ApiResult<Int>>

    @GET(Constants.GET_TOTAL_AMOUNT_SALES)
    suspend fun getTotalAmountSales(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Response<ApiResult<Int>>

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
}