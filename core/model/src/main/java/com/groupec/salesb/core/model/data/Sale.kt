package com.groupec.salesb.core.model.data

import java.util.Date

data class Sale(
    val id: Int ? = null,
    val datevente: Date? = null,
    val totalprix: Double,
    val datemodif: String ? = null,
    val userid: Int ? = null,
    val username: String ? = null,
    val details: List<SaleDetail>
)

data class SaleDetail(
    val produitid: Int,
    val libelle: String ? = null,
    val qte: Double,
    val prix: Double
)
