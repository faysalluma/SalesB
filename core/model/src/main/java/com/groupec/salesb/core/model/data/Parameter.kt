package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Parameter(
    val devise: String = "",
    val raisonsociale: String = "",
    val adresse: String = "",
    val telephone: String = "",
    val email: String ? = "",
    val ifu: String ? = "",
    val website: String ? = "",
    val typeentreprise: String = "",
    val offline: Boolean = false,
    val primarycolor: String = "",
    val secondarycolor: String = "",
    val loadproducts: Boolean = false,
    val tva: Double = 0.0
) : Parcelable