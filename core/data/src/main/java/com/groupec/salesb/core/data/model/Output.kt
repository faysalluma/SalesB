package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.network.model.OutputItemResponse
import com.groupec.salesb.core.network.model.OutputResponse


fun OutputResponse.toOutputList(): List<Output> = outputs.map { it.toOutput() }

fun OutputItemResponse.toOutput() = Output(
    id = id,
    datecreation = datecreation,
    description = description,
    prix = prix,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom
)

