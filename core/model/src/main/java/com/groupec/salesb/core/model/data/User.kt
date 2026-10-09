package com.groupec.salesb.core.model.data

data class User(
    val userId: Int,
    val fullName: String,
    val email: String,
    val address: String?,
    val phone: String?,
    val active: Boolean,
    val firstLogin: Boolean,
    val companies: List<UserCompany>
)
