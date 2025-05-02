package com.groupec.salesb.core.data.model

import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.network.model.CategoryItemResponse
import com.groupec.salesb.core.network.model.CategoryResponse


fun CategoryResponse.toCategorieList(): List<Category> = categories.map { it.toCategorie() }

fun CategoryItemResponse.toCategorie() = Category(
    id = id,
    datecreation = datecreation,
    libelle = libelle,
    description = description,
    datemodif = datemodif,
    userid = user?.id,
    username = user?.nomprenom
)

