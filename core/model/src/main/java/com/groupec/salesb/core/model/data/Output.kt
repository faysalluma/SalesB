package com.groupec.salesb.core.model.data

import java.util.Date

data class Output(
    val id: Int ? = null,
    val datecreation: Date? = null,
    val description: String,
    val prix: Double,
    val datemodif: Date ? = null,
    val userid: Int ? = null,
    val username: String ? = null
)
