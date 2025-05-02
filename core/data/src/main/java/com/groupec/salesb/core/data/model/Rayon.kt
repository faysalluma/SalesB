package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.network.model.RayonItemResponse
import com.groupec.salesb.core.network.model.RayonResponse


fun RayonResponse.toRayonList(): List<Rayon> = rayons.map { it.toRayon() }

fun RayonItemResponse.toRayon() = Rayon(
    id = id,
    datecreation = datecreation,
    libelle = libelle,
    description = description,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom
)

