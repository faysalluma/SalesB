package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Output(
    val id: Int ? = null,
    val datecreation: Date? = null,
    val description: String,
    val prix: Double,
    val datemodif: Date ? = null,
    val userid: Int ? = null,
    val username: String ? = null
): Parcelable
