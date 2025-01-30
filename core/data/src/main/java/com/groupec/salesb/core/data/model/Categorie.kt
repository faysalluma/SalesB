package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Categorie
import com.groupec.salesb.core.network.model.CategorieItemResponse
import com.groupec.salesb.core.network.model.CategorieResponse


fun CategorieResponse.toCategorieList(): List<Categorie> = categories.map { it.toCategorie() }

fun CategorieItemResponse.toCategorie() = Categorie(
    id = id,
    datecreation = datecreation,
    libelle = libelle,
    description = description,
    datemodif = datemodif,
    userid = userid
)

