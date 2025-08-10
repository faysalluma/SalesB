package com.groupec.salesb.core

import android.content.Context

enum class Privileges(val values: Map<Int, Approval>) {
    Home(
        mapOf(
            1 to Approval.AUTHORIZE_VIEW,
            2 to Approval.STAT_PERIODIC,
            3 to Approval.STAT_NON_PERIODIC,
            4 to Approval.STAT_CHART,
        )
    ),
    Sale(
        mapOf(
            5 to Approval.AUTHORIZE_VIEW,
            6 to Approval.AUTHORIZE_ADD,
            7 to Approval.AUTHORIZE_EDIT,
            8 to Approval.AUTHORIZE_DELETE,
        )
    ),
    Product(
        mapOf(
            9 to Approval.AUTHORIZE_VIEW,
            10 to Approval.AUTHORIZE_ADD,
            11 to Approval.AUTHORIZE_EDIT,
            12 to Approval.AUTHORIZE_DELETE,
        )
    ),
    Statistic(
        mapOf(
            13 to Approval.AUTHORIZE_VIEW,
        )
    ),
    Entrie(
        mapOf(
            14 to Approval.AUTHORIZE_VIEW,
            15 to Approval.AUTHORIZE_ADD,
            16 to Approval.AUTHORIZE_EDIT,
            17 to Approval.AUTHORIZE_DELETE,
        )
    ),
    UserSettings(
        mapOf(
            18 to Approval.AUTHORIZE_VIEW,
            19 to Approval.AUTHORIZE_ADD,
            20 to Approval.AUTHORIZE_EDIT,
            21 to Approval.AUTHORIZE_DELETE,
        )
    );

    fun getKeyByApproval(approval : Approval) = values.entries.find { it.value == approval }?.key

    fun getKeysByApprovals(approvals: List<Approval>): List<Int> {
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