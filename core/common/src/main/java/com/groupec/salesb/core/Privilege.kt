package com.groupec.salesb.core

import android.content.Context
import com.groupec.salesb.common.R

enum class Screen(val privileges: List<Privilege>) {
    Home(
        listOf(
            Privilege.AUTHORIZE_VIEW,
            Privilege.TOTAL_SALE_PRODUCT,
            Privilege.TOTAL_ENTRIE_PRODUCT,
            Privilege.STAT_CHART
        )
    ),
    Sale(
        listOf(
            Privilege.AUTHORIZE_VIEW,
            Privilege.AUTHORIZE_ADD,
            Privilege.AUTHORIZE_EDIT,
            Privilege.AUTHORIZE_DELETE
        )
    ),
    Product(
        listOf(
            Privilege.AUTHORIZE_VIEW,
            Privilege.AUTHORIZE_ADD,
            Privilege.AUTHORIZE_EDIT,
            Privilege.AUTHORIZE_DELETE
        )
    ),
    Statistic(
        listOf(
            Privilege.AUTHORIZE_VIEW
        )
    ),
    Entrie(
        listOf(
            Privilege.AUTHORIZE_VIEW,
            Privilege.AUTHORIZE_ADD,
            Privilege.AUTHORIZE_EDIT,
            Privilege.AUTHORIZE_DELETE
        )
    ),
    UserSettings(
        listOf(
            Privilege.AUTHORIZE_VIEW,
            Privilege.AUTHORIZE_ADD,
            Privilege.AUTHORIZE_EDIT,
            Privilege.AUTHORIZE_DELETE
        )
    )
}
enum class Privilege(val id: Int, val titleRes: Int) {

    // Common
    AUTHORIZE_VIEW(1, R.string.authorize_view),
    AUTHORIZE_ADD(2, R.string.authorize_add),
    AUTHORIZE_EDIT(3, R.string.authorize_edit),
    AUTHORIZE_DELETE(4, R.string.authorize_delete),

    // Home
    TOTAL_SALE_PRODUCT(5, R.string.total_sale_product),
    TOTAL_ENTRIE_PRODUCT(6, R.string.total_sale_product),
    STAT_CHART(7, R.string.stat_chart);

    fun getTitle(context: Context) = context.getString(titleRes)
}