package com.groupec.salesb.core.network.retrofit

import com.groupec.salesb.core.network.model.NetworkResult
import com.groupec.salesb.core.network.model.ParameterResponse
import com.groupec.salesb.core.network.model.UserItemResponse
import com.groupec.salesb.core.network.model.UserResponse
import com.groupec.salesb.core.network.retrofit.common.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET(Constants.GET_PARAMETER)
    suspend fun getParameter() : Response<ParameterResponse>

    @GET(Constants.GET_DEFAULT_USER)
    suspend fun getDefaultUser() : Response<UserResponse>

    @GET(Constants.GET_USER_BY_EMAIL)
    suspend fun getUserByEmail(@Path("email") email: String) : Response<NetworkResult<UserItemResponse>>
}