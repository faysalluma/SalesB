package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Sale(
    val id: Int ? = null,
    val datevente: Date? = null,
    val totalprix: Double,
    val datemodif: String ? = null,
    val userid: Int ? = null,
    val username: String ? = null,
    val details: List<SaleDetail>
) : Parcelable

@Parcelize
data class SaleDetail(
    val id: Int,
    val libelle: String ? = null,
    val qte: Double,
    val prix: Double
): Parcelable
