package com.groupec.salesb.core.network.retrofit.common

import com.groupec.salesb.core.Constants

class Constants {
    companion object {
        const val BASE_URL = Constants.BASE_URL

        // Get endpoint
        const val GET_PARAMETER = "parameter"
        const val GET_DEFAULT_USER = "defaultUser"
        const val GET_USER_BY_EMAIL = "checkLoginByEmail/{email}"
        const val GET_USER_BY_ID = "checkLoginById/{userid}"
        const val GET_TOTAL_SALES = "getTotalSales/{startDate}/{endDate}"
        const val GET_TOTAL_AMOUNT_SALES = "getTotalAmountSales/{startDate}/{endDate}"
        const val GET_TOTAL_AMOUNT_OUTPUTS = "getTotalAmountOutputs/{startDate}/{endDate}"
        const val GET_TOTAL_PRODUCTS = "getTotalProducts"
        const val GET_TOP_SALE_PRODUCTS = "getTopSaleProducts/{startDate}/{endDate}"
        const val GET_ALERT_SEUIL = "getAlertSeuil"
        const val GET_TOTAL_SALE_DAY = "getTotalSaleMorningEvening/{date}"
        const val GET_TOTAL_SALE_BY_DATE = "getTotalSalesByDate/{startDate}/{endDate}"
        const val GET_PRODUCTS = "getProducts"
        const val GET_PAGED_CATEGORIES = "getPagedCategories"
        const val GET_OUTPUTS = "getOutputs"
        const val GET_CATEGORIES = "getCategories"
        const val GET_PRODUCTS_LOW_INVENTORY = "getProductsWithLowInventory"
        const val GET_RAYONS = "getRayons"
        const val GET_SALES = "getSales"
        const val FORGOT_PASSWORD = "forgotPassword/{email}"
        const val GET_USERS= "getUsers"

        // Post endpoint
        const val ADD_PRODUCT = "addProduct"
        const val ADD_SALE = "addSale"
        const val ADD_CATEGORY = "addCategory"
        const val ADD_RAYON = "addRayon"
        const val ADD_OUTPUT = "addOutput"
        const val ADD_USER = "addUser"

        // Put endpoint
        const val PUT_CHANGE_PASSWORD = "changePassword/{userid}"

        // Delete endpoint
        const val DELETE_PRODUCT = "deleteProduct/{productid}"
        const val DELETE_CATEGORY = "deleteCategory/{categoryid}"
        const val DELETE_RAYON = "deleteRayon/{rayonid}"
        const val DELETE_OUTPUT = "deleteOutput/{outputid}"
        const val DELETE_USER = "deleteUser/{userid}"
    }
}