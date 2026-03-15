package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Parameter(
    val id: Int = 0,
    val logo: String ? = "",
    val raisonsociale: String = "",
    val entreprisetype: Int = 0,
    val expirationdate: String = "",
    val ifu: String ? = "",
    val adresse: String? = "",
    val telephone: String? = "",
    val email: String ? = "",
    val website: String ? = "",
    val devise: String = "",
    val tva: Double = 0.0,
    val offline: Boolean = false,
    val defaultpaymenttype: String = "",
    val termsandconditions: Boolean = true,
    val serviceview: Boolean = false,
    val showimageonproduct: Boolean = false,
    val useintforpriceandamout: Boolean = false,
    val activepaymentmode: Boolean = true,
    val activeprinter: Boolean = false
) : Parcelable
