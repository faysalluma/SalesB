package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.network.model.ParamItemResponse
import com.groupec.salesb.core.network.model.ParameterResponse


fun ParameterResponse.toParameter(): Parameter {
    return  parameter.toParameter()
}

fun ParamItemResponse.toParameter(): Parameter {
    return Parameter(
        id = id,
        logo = logo,
        devise = devise,
        raisonsociale = raisonsociale,
        adresse = adresse,
        email = email,
        telephone = telephone,
        ifu = ifu,
        website = website,
        entreprisetype = entreprisetype,
        expirationdate = expirationdate,
        offline = (offline == 1),
        showimageonproduct = (showimageonproduct == 1),
        defaultpaymenttype = defaultpayment ?: "",
        tva = tva,
        useintforpriceandamout = (useintforpriceandamout == 1),
        activepaymentmode = (activepaymentmode == 1),
        activeClient = (activeclient == 1),
        activeprinter = (activeprinter == 1)
    )
}
