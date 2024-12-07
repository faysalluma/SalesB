package com.groupec.salesb.core.model.data

import java.util.Date

data class Product(
    val id: Int,
    val reference: String ? = null,
    val libelle: String,
    val description: String ? = null,
    val image: String ? = null,
    val prixht: Double ? = null,
    val prixttc: Double,
    val qtestock: Int ? = 0,
    val stockmini: Int ? = null,
    val categorieid: Int ? = null,
    val rayonid: Int ? = null,
    val fournisseurid: Int ? = null,
    val tvaid: Int ? = null,
    val datemodif: Date ? = null,
    val userid: Int ? = null
)
