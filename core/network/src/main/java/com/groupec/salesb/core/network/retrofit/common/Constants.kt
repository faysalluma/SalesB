package com.groupec.salesb.core.network.retrofit.common

class Constants {
    companion object {
        // For emulator user 10.0.2.2 --- (192, 172) for wampserver --- Don't use 10.188 ...

        const val BASE_URL = "http://192.168.1.144/SalesBApi/public/"

        // Get endpoint
        const val GET_PARAMETER = "parameter"
        const val GET_DEFAULT_USER = "defaultUser"
        const val GET_USER_BY_EMAIL = "checkLoginByEmail/{email}"
        const val GET_USER_BY_ID = "checkLoginById/{userid}"
        const val GET_TOTAL_SALES = "getTotalSales/{startDate}/{endDate}"
        const val GET_TOTAL_AMOUNT_SALES = "getTotalAmountSales/{startDate}/{endDate}"
        const val GET_TOTAL_PRODUCTS = "getTotalProducts"
        const val GET_TOP_SALE_PRODUCTS = "getTopSaleProducts/{startDate}/{endDate}"
        const val GET_ALERT_SEUIL = "getAlertSeuil"
        const val GET_TOTAL_SALE_DAY = "getTotalSaleMorningEvening/{date}"
        const val GET_TOTAL_SALE_BY_DATE = "getTotalSalesByDate/{startDate}/{endDate}"

        // Put endpoint
        const val PUT_CHANGE_PASSWORD = "changePassword/{userid}"
    }
}