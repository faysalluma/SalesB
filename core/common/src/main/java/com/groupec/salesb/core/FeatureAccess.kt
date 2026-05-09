package com.groupec.salesb.core

import com.groupec.cleanarchitecture.core.config.AppConfigHolder

object FeatureAccess {
    private val features
        get() = AppConfigHolder.current.features

    val isSubscriptionScreenEnabled: Boolean
        get() = features.subscriptionScreenEnabled

    val isClientManagementEnabled: Boolean
        get() = features.clientManagementEnabled

    val isRayonManagementEnabled: Boolean
        get() = features.rayonManagementEnabled

    fun canExport(isProActive: Boolean): Boolean =
        features.exportEnabled && isProActive

    fun canPrintReceipt(isProActive: Boolean): Boolean =
        features.receiptPrintingEnabled && isProActive

    fun canCreateProduct(
        isProActive: Boolean,
        totalProductsCount: Int,
        freeProductLimit: Int = 10,
    ): Boolean =
        !features.productLimitEnabled || isProActive || totalProductsCount < freeProductLimit

    fun canCreateSale(
        isProActive: Boolean,
        hasReachedFreeMonthlySalesLimit: Boolean,
    ): Boolean =
        !features.saleLimitEnabled || isProActive || !hasReachedFreeMonthlySalesLimit
}
