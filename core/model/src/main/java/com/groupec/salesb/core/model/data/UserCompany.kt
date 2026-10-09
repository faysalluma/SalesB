package com.groupec.salesb.core.model.data

data class UserCompany(
    val memberId: Long,
    val companyId: Int,
    val role: String,
    val companyName: String?,
    val image: String?,
    val businessType: String,
    val permissions: List<String>
)
