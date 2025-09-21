package com.groupec.salesb.core.model.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Invoicing(
    val fullName: String = "",
    val address: String = "",
    val email: String =""
) : Parcelable
