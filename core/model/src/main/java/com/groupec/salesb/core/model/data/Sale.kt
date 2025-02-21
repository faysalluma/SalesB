package com.groupec.salesb.core.model.data

data class Sale(
    val id: Int ? = null,
    val datevente: String ? = null,
    val totalprix: Double,
    val datemodif: String ? = null,
    val userid: Int ? = null,
    val details: List<SaleDetail>
)

data class SaleDetail(val produitid: Int, val qte: Double, val prix: Double)
