package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Employee
import com.groupec.salesb.core.network.model.EmployeeItemResponse

fun EmployeeItemResponse.toEmployee(): Employee = Employee(
    memberid = memberId, userid = userId, entrepriseid = companyId, role = role,
    nomprenom = nomprenom, email = email, adresse = adresse, tel = tel,
    actif = actif == 1, permissions = permissions,
    datecreation = datecreation, datemodif = datemodif
)
