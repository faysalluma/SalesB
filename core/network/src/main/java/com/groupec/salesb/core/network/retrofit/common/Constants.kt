package com.groupec.salesb.core.network.retrofit.common

class Constants {
    companion object {
        // For emulator user 10.0.2.2 --- (192, 172) for wampserver --- Don't use 10.188 ...

        const val BASE_URL = "http://192.168.1.144/SalesBApi/public/"

        // Get endpoint
        const val GET_PARAMETER = "parameter"
        const val GET_DEFAULT_USER = "defaultUser"
        const val GET_USER_BY_EMAIL = "checkLogin/{email}"
    }
}