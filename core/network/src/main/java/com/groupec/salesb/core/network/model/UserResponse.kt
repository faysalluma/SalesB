package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("user")
    val user : UserItemResponse
)
data class UserItemResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nomprenom")
    val nomprenom: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("adresse")
    val adresse: String,

    @SerializedName("tel")
    val tel: String,

    @SerializedName("privilege")
    val privilege: String,

    @SerializedName("actif")
    val actif: Int,

    @SerializedName("firstlogin")
    val firstlogin: Int
)