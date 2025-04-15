package com.groupec.salesb.core

class Constants {
    companion object {
        // For emulator user 10.0.2.2 --- (192, 172) for wampserver --- Don't use 10.188 ...
        const val SERVER_URL = "http://192.168.1.37/SalesBApi/"
        // const val SERVER_URL = "https://salesbapi.groupec.net/"
        const val BASE_URL = SERVER_URL.plus("public/")
        const val UPLOAD_URL = SERVER_URL.plus("uploads/")
        const val APP_LINK = "https://www.salesb.groupec.net/resetapppassword"
    }
}