package com.groupec.salesb.core

import android.content.Context

enum class Privileges(val title: String? = null, val values: Map<String, Approval>) {
    // Key coded as follows:
    // First letter (Screen First letter)
    // Second letter (If two screens have the same first letter,
    // it is equal to 0 for the first screen in the order of insertion in Privileges and 1 for the second screen, and so on.)
    // First letter (Approval order: 1 for AUTHORIZE_VIEW, and so on)
    Home(
        values = mapOf(
            "H01" to Approval.AUTHORIZE_VIEW,
            "H02" to Approval.STAT_PERIODIC,
            "H03" to Approval.STAT_NON_PERIODIC,
            "H04" to Approval.STAT_CHART
        )
    ),
    Sale(
        values = mapOf(
            "S01" to Approval.AUTHORIZE_VIEW
        )
    ),
    MySales(
        values = mapOf(
            "M01" to Approval.AUTHORIZE_VIEW,
            "M02" to Approval.AUTHORIZE_ADD,
            "M03" to Approval.AUTHORIZE_EDIT,
            "M04" to Approval.AUTHORIZE_DELETE
        )
    ),
    Product(
        values = mapOf(
            "P01" to Approval.AUTHORIZE_VIEW,
            "P02" to Approval.AUTHORIZE_ADD,
            "P03" to Approval.AUTHORIZE_EDIT,
            "P04" to Approval.AUTHORIZE_DELETE,
        )
    ),
    Outputs(
        values = mapOf(
            "O01" to Approval.AUTHORIZE_VIEW,
            "O02" to Approval.AUTHORIZE_ADD,
            "O03" to Approval.AUTHORIZE_EDIT,
            "O04" to Approval.AUTHORIZE_DELETE
        )
    ),
    Parameters(
        values = mapOf(
            "P11" to Approval.AUTHORIZE_VIEW,
            "P12" to Approval.AUTHORIZE_ADD,
            "P13" to Approval.AUTHORIZE_EDIT,
            "P14" to Approval.AUTHORIZE_DELETE
        )
    ),
    UserSettings(
        values = mapOf(
            "U01" to Approval.AUTHORIZE_VIEW
        )
    );

    fun getKeyByApproval(approval : Approval) = values.entries.find { it.value == approval }?.key

    fun getKeysByApprovals(approvals: List<Approval>): List<String> {
        return approvals.mapNotNull { approval ->
            values.entries.find { it.value == approval }?.key
        }
    }
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