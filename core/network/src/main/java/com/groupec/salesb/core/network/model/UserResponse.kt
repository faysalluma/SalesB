package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class EmployeeResponse(
    @SerializedName("members") val members: List<EmployeeItemResponse>
)
data class EmployeeItemResponse(
    @SerializedName("memberid") val memberId: Long,
    @SerializedName("userid") val userId: Int,
    @SerializedName("entrepriseid") val companyId: Int,
    @SerializedName("role") val role: String,
    @SerializedName("nomprenom") val nomprenom: String,
    @SerializedName("email") val email: String,
    @SerializedName("adresse") val adresse: String?,
    @SerializedName("tel") val tel: String?,
    @SerializedName("actif") val actif: Int,
    @SerializedName("permissions") val permissions: List<String>,
    @SerializedName("datecreation") val datecreation: Date? = null,
    @SerializedName("datemodif") val datemodif: Date? = null
)

data class UserReducedResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nomprenom")
    val nomprenom: String
)
