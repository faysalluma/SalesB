package com.groupec.salesb.core.network.model

import com.google.gson.annotations.SerializedName

/** Global account returned after server-side password verification. */
data class AccountResponse(
    @SerializedName("userid") val userId: Int,
    @SerializedName("nomprenom") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("adresse") val address: String?,
    @SerializedName("tel") val phone: String?,
    @SerializedName("actif") val active: Int,
    @SerializedName("firstlogin") val firstLogin: Int,
    @SerializedName("companies") val companies: List<CompanyMembershipResponse>
)

data class CompanyMembershipResponse(
    @SerializedName("memberid") val memberId: Long,
    @SerializedName("entrepriseid") val companyId: Int,
    @SerializedName("role") val role: String,
    @SerializedName("raisonsociale") val companyName: String?,
    @SerializedName("image") val image: String?,
    @SerializedName("businessType") val businessType: String,
    @SerializedName("permissions") val permissions: List<String>
)

data class CompaniesResponse(
    @SerializedName("companies") val companies: List<CompanyMembershipResponse>
)
