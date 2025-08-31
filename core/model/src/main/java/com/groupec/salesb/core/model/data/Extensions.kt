package com.groupec.salesb.core.model.data

fun String.toStringList(delimiter: String = ","): List<String> {
    return this.split(delimiter).map { it.trim() }
}