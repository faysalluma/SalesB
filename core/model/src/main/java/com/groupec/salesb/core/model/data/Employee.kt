package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

/** A company's member, not the authenticated global user. */
@Parcelize
data class Employee(
    val memberid: Long,
    val userid: Int,
    val entrepriseid: Int,
    val role: String,
    val nomprenom: String,
    val email: String,
    val adresse: String?,
    val tel: String?,
    val actif: Boolean,
    val permissions: List<String>,
    val datecreation: Date? = null,
    val datemodif: Date? = null
) : Parcelable
