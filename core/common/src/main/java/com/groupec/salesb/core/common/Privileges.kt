package com.groupec.salesb.core

import android.content.Context

enum class Privileges(val titleRes: Int? = null, val values: Map<String, Approval>) {
    // Key coded as follows:
    // First letter (Screen First letter)
    // Second letter (If two screens have the same first letter,
    // it is equal to 0 for the first screen in the order of insertion in Privileges and 1 for the second screen, and so on.)
    // First letter (Approval order: 1 for AUTHORIZE_VIEW, and so on)
    // Don't save the key of AUTHORIZE_VIEW into database when Privilige items contains order value like (AUTHORIZE_ADD, AUTHORIZE_EDI, etc.)
    Home(
        titleRes = R.string.menu_home_view,
        values = mapOf(
            "H01" to Approval.AUTHORIZE_VIEW,
            "H02" to Approval.STAT_PERIODIC,
            "H03" to Approval.STAT_NON_PERIODIC,
            "H04" to Approval.STAT_CHART
        )
    ),
    Sale(
        titleRes = R.string.menu_save_sale_view,
        values = mapOf(
            "S01" to Approval.AUTHORIZE_VIEW
        )
    ),
    MySales(
        titleRes = R.string.menu_my_sales_view,
        values = mapOf(
            "M01" to Approval.AUTHORIZE_VIEW,
            "M02" to Approval.AUTHORIZE_ADD,
            "M03" to Approval.AUTHORIZE_EDIT,
            "M04" to Approval.AUTHORIZE_DELETE
        )
    ),
    Product(
        titleRes = R.string.menu_product_view,
        values = mapOf(
            "P01" to Approval.AUTHORIZE_VIEW,
            "P02" to Approval.AUTHORIZE_ADD,
            "P03" to Approval.AUTHORIZE_EDIT,
            "P04" to Approval.AUTHORIZE_DELETE,
        )
    ),
    Outputs(
        titleRes = R.string.menu_outputs_view,
        values = mapOf(
            "O01" to Approval.AUTHORIZE_VIEW,
            "O02" to Approval.AUTHORIZE_ADD,
            "O03" to Approval.AUTHORIZE_EDIT,
            "O04" to Approval.AUTHORIZE_DELETE
        )
    ),
    HandleService(
        titleRes = R.string.handle_services,
        values = mapOf(
            "H11" to Approval.AUTHORIZE_VIEW
        )
    ),
    UserSettings(
        titleRes = R.string.manage_your_account_view,
        values = mapOf(
            "U01" to Approval.AUTHORIZE_VIEW
        )
    ),
    Category(
        titleRes = R.string.menu_category_view,
        values = mapOf(
            "C01" to Approval.AUTHORIZE_VIEW,
            "C02" to Approval.AUTHORIZE_ADD,
            "C03" to Approval.AUTHORIZE_EDIT,
            "C04" to Approval.AUTHORIZE_DELETE
        )
    ),
    Client(
        titleRes = R.string.menu_client_view,
        values = mapOf(
            "C11" to Approval.AUTHORIZE_VIEW,
            "C12" to Approval.AUTHORIZE_ADD,
            "C13" to Approval.AUTHORIZE_EDIT,
            "C14" to Approval.AUTHORIZE_DELETE
        )
    ),
    Rayon(
        titleRes = R.string.menu_rayon_view,
        values = mapOf(
            "R01" to Approval.AUTHORIZE_VIEW,
            "R02" to Approval.AUTHORIZE_ADD,
            "R03" to Approval.AUTHORIZE_EDIT,
            "R04" to Approval.AUTHORIZE_DELETE
        )
    );

    fun getKeyByApproval(approval : Approval) : String? = values.entries.find { it.value == approval }?.key

    fun getKeysByApprovals(approvals: List<Approval>): List<String> {
        return approvals.mapNotNull { approval ->
            values.entries.find { it.value == approval }?.key
        }
    }

    fun checkKeyByApprovals(approvals: List<Approval>): Boolean {
        return approvals.any { approval ->
            values.containsValue(approval)
        }
    }

    fun getTitle(context: Context) = titleRes?.let { context.getString(it) } ?: this.name
}
enum class Approval(val titleRes: Int) {

    // Common
    AUTHORIZE_VIEW(R.string.authorize_view),
    AUTHORIZE_ADD(R.string.authorize_add),
    AUTHORIZE_EDIT(R.string.authorize_edit),
    AUTHORIZE_DELETE(R.string.authorize_delete),

    // Home
    STAT_PERIODIC(R.string.view_stat_periodic),
    STAT_NON_PERIODIC(R.string.view_stat_non_periodic),
    STAT_CHART(R.string.stat_chart);

    fun getTitle(context: Context) = context.getString(titleRes)
}
