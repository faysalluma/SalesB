package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class UserResponse(
    @SerializedName("users")
    val users : ArrayList<UserItemResponse>
)
data class UserItemResponse(
    @SerializedName("userid")
    val id: Int ? = null,

    @SerializedName("nomprenom")
    val nomprenom: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("reset_password")
    val reset_password: String ? = null,

    @SerializedName("reset_expires")
    val reset_expires: String ? = null,

    @SerializedName("adresse")
    val adresse: String,

    @SerializedName("tel")
    val tel: String,

    @SerializedName("privilege")
    val privilege:  String ? = null,

    @SerializedName("actif")
    val actif: Int,

    @SerializedName("firstlogin")
    val firstlogin: Int,

    @SerializedName("datecreation")
    val datecreation: Date? = null,

    @SerializedName("datemodif")
    val datemodif: Date ? = null,

    @SerializedName("productId")
    val productId: String? = null,

    @SerializedName("purchaseToken")
    val purchaseToken: String? = null,
)

data class UserReducedResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nomprenom")
    val nomprenom: String
)