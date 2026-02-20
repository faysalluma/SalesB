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
    val defaultpaymenttype: String = "",
    val tva: Double = 0.0,
    val termsandconditions: Boolean = true,
    val serviceview: Boolean = false,
    val showimageonproduct: Boolean = false,
    val useintforpriceandamout: Boolean = false,
    val activepaymentmode: Boolean = false,
    val activeprinter: Boolean = false
) : Parcelable
