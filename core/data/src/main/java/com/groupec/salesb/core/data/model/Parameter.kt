package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.network.model.ParamItemResponse
import com.groupec.salesb.core.network.model.ParameterResponse


fun ParameterResponse.toParameter(): Parameter {
    return  parameter.toParameter()
}

fun ParamItemResponse.toParameter(): Parameter {
    return Parameter(devise = devise, raisonsociale = raisonsociale, adresse = adresse, email = email,
        telephone = telephone, ifu = ifu, website = website, typeentreprise = typeentreprise, offline = (offline == 1),
        primarycolor = primarycolor, secondarycolor = secondarycolor, loadproducts = (loadproducts == 1))
}