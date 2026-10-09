package com.groupec.salesb.core.network.retrofit.common

import com.groupec.salesb.core.Constants as CommonConstants

class Constants {
    companion object {
        val BASE_URL: String
            get() = CommonConstants.BASE_URL

        val NETWORK_TIMEOUT_SECONDS: Long
            get() = CommonConstants.NETWORK_TIMEOUT_SECONDS

        val IS_DEBUG: Boolean
            get() = CommonConstants.IS_DEBUG


        // Get endpoint
        const val GET_COMPANIES = "companies/{userid}"
        const val GET_PARAMETER = "parameter/{userid}/{entrepriseid}"
        const val GET_COMPANY_SUBSCRIPTION = "subscription/{userid}/{entrepriseid}"
        const val GET_TOTAL_SALES = "getTotalSales/{startDate}/{endDate}/{userid}"
        const val GET_TOTAL_AMOUNT_SALES = "getTotalAmountSales/{startDate}/{endDate}/{userid}"
        const val GET_TOTAL_AMOUNT_OUTPUTS = "getTotalAmountOutputs/{startDate}/{endDate}/{userid}"
        const val GET_TOTAL_PRODUCTS = "getTotalProducts"
        const val GET_TOP_SALE_PRODUCTS = "getTopSaleProducts/{startDate}/{endDate}/{userid}"
        const val GET_ALERT_SEUIL = "getAlertSeuil"
        const val GET_TOTAL_SALE_DAY = "getTotalSaleMorningEvening/{date}/{userid}"
        const val GET_TOTAL_SALE_BY_DATE = "getTotalSalesByDate/{startDate}/{endDate}/{userid}"
        const val GET_RECENT_ACTIVITIES = "getRecentActivities"
        const val GET_PAGED_PRODUCTS = "getPagedProducts"
        const val GET_PRODUCTS = "getProducts"
        const val GET_PAGED_CATEGORIES = "getPagedCategories"
        const val GET_PAGED_CLIENTS = "getPagedClients"
        const val GET_PAGED_OUTPUTS = "getPagedOutputs"
        const val GET_OUTPUTS = "getOutputs"
        const val GET_CATEGORIES = "getCategories"
        const val GET_CLIENTS = "getClients"
        const val GET_ALL_CATEGORIES = "getAllCategories"
        const val GET_ALL_CLIENTS = "getAllClients"
        const val GET_PRODUCTS_LOW_INVENTORY = "getProductsWithLowInventory"
        const val GET_RAYONS = "getRayons"
        const val GET_PAGED_SALES = "getPagedSales"

        const val GET_SALES = "getSales"
        const val FORGOT_PASSWORD = "forgotPassword/{email}"
        const val GET_PAGED_USERS= "company-members/{userid}/{entrepriseid}"

        // Post endpoint
        const val CHECK_LOGIN = "checkLogin"
        const val ADD_PRODUCT = "addProduct"
        const val ADD_SALE = "addSale"
        const val ADD_CATEGORY = "addCategory"
        const val ADD_CLIENT = "addClient"
        const val ADD_RAYON = "addRayon"
        const val ADD_OUTPUT = "addOutput"
        const val ADD_USER = "company-members/{userid}/{entrepriseid}"
        const val UPDATE_MEMBER = "company-members/{userid}/{entrepriseid}/{memberid}"
        const val ADD_SIGNUP_CONFIGURATION = "addSignupConfiguration"

        // Put endpoint
        const val CHANGE_ACCOUNT_PASSWORD = "changePassword"
        const val PUT_PARAMETER = "parameter/{userid}/{entrepriseid}"
        const val PUT_USER_SUBSCRIPTION_STATUS = "subscription/{userid}/{entrepriseid}/google-play/verify"

        // Delete endpoint
        const val DELETE_PRODUCT = "deleteProduct/{productid}"
        const val DELETE_CATEGORY = "deleteCategory/{categoryid}"
        const val DELETE_CLIENT = "deleteClient/{clientid}"
        const val DELETE_RAYON = "deleteRayon/{rayonid}"
        const val DELETE_OUTPUT = "deleteOutput/{outputid}"
        const val DELETE_USER = "company-members/{userid}/{entrepriseid}/{memberid}"
    }
}
