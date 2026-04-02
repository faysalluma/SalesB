package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.network.model.ClientItemResponse
import com.groupec.salesb.core.network.model.ClientResponse

fun ClientResponse.toClientList(): List<Client> = clients.map { it.toClient() }

fun ClientItemResponse.toClient() = Client(
    id = id,
    datecreation = datecreation,
    nomprenom = nomprenom,
    adresse = adresse,
    telephone = telephone,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom
)
