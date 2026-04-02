package com.groupec.salesb.core.model.data

import android.os.Parcelable
import java.util.Date
import kotlinx.parcelize.Parcelize

@Parcelize
data class Client(
    val id: Int ? = null,
    val datecreation: Date? = null,
    val nomprenom: String,
    val adresse: String ? = null,
    val telephone: String ? = null,
    val datemodif: Date ? = null,
    val userid: Int ? = null,
    val username: String ? = null
) : Parcelable
