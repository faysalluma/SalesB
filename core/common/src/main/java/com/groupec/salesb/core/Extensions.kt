package com.groupec.salesb.core

/* Replace , by . to have good dougle format */
fun String.normalizeDecimalSeparator(): String {
    return this.replace(",", ".").filter { it.isDigit() || it == '.' }
}

fun String.allowOnlyDigits(): String {
    return this.filter { it.isDigit() }
}