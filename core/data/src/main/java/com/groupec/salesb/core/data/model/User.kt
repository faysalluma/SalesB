package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.UserCompany
import com.groupec.salesb.core.network.model.AccountResponse
import com.groupec.salesb.core.network.model.CompanyMembershipResponse

fun AccountResponse.toAuthenticatedUser(): User = User(
    userId = userId,
    fullName = fullName,
    email = email,
    address = address,
    phone = phone,
    active = active == 1,
    firstLogin = firstLogin == 1,
    companies = companies.map { it.toUserCompany() }
)

fun CompanyMembershipResponse.toUserCompany(): UserCompany = UserCompany(
    memberId = memberId,
    companyId = companyId,
    role = role,
    companyName = companyName,
    image = image,
    businessType = businessType,
    permissions = permissions.toList()
)
