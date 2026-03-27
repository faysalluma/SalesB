package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class User(
    val id: Int? = null,
    val nomprenom: String,
    val email: String,
    val password: String,
    val reset_password: String ? = null,
    val reset_expires: String ? = null,
    val adresse: String ? = null,
    val tel: String? = null,
    val privilege: String ? = null,
    val actif: Boolean,
    val firstlogin: Boolean,
    val datecreation: Date ? = null,
    val datemodif: Date ? = null,
    val synchronised: Boolean = false,
    val langMessageEn: Boolean = true
): Parcelable
