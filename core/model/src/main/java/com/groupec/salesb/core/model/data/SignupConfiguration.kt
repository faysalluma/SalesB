package com.groupec.salesb.core.model.data

data class SignupConfiguration(
    val fullName: String,
    val email: String,
    val password: String,
    val companyName: String,
    val companyType: Int,
    val companyEmail: String?,
    val address: String?,
    val phone: String?,
    val ifu: String?,
    val website: String?,
    val devise: String,
    val tva: Double,
    val useIntForPriceAndAmount: Int,
    val showImageOnProduct: Int,
    val activePaymentMode: Int,
    val defaultpayment: String?,
    val activePrinter: Int
)
