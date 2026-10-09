package com.groupec.salesb.core.model.data

/** Local user/company context, not an authentication token. */
data class UserSession(
    val account: User? = null,
    val selectedCompanyId: Int? = null
) {
    val selectedCompany: UserCompany?
        get() = account?.companies?.firstOrNull { it.companyId == selectedCompanyId }
}
