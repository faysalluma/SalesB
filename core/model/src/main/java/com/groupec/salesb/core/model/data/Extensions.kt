package com.groupec.salesb.core.model.data

fun String.toIntList(delimiter: String = ","): List<Int> {
    return this.split(delimiter).map { it.trim().toInt() }
}