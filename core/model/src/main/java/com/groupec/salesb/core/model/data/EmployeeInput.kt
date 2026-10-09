package com.groupec.salesb.core.model.data

import com.google.gson.annotations.SerializedName

data class EmployeeInput(
    @SerializedName("memberid") val memberId: Long? = null,
    @SerializedName("nomprenom") val nomprenom: String,
    @SerializedName("email") val email: String,
    @SerializedName("adresse") val adresse: String?,
    @SerializedName("tel") val tel: String?,
    @SerializedName("password") val password: String?,
    @SerializedName("permissions") val permissions: List<String>,
    @SerializedName("actif") val actif: Int,
    @SerializedName("langMessageEn") val langMessageEn: Int
) {
    override fun toString(): String = "EmployeeInput(memberId=$memberId, credentials=REDACTED)"
}
