package com.groupec.salesb.core

import java.math.RoundingMode

/* Replace , by . to have good dougle format */
fun String.normalizeDecimalSeparator(): String {
    return this.replace(",", ".")
        .filter { it.isDigit() || it == '.' }
        .let { if (it.count { it == '.' } > 1) it.substring(0, it.lastIndexOf('.')) else it }
}

fun String.allowOnlyDigits(): String {
    return this.filter { it.isDigit() }
}

fun Double.autoRound(): String {
    var formatted = "%.2f".format(this).replace(",", ".")

    // Si le nombre est un entier exact avant l'arrondi ou très proche d'un entier (dans un intervalle de ±0.000001), on garde le .0
    if (kotlin.math.abs(this - this.toInt().toDouble()) < 0.000001) {
        return formatted
    }

    // Sinon, on enlève les zéros superflus
    while (formatted.endsWith("0")) {
        formatted = formatted.dropLast(1)
    }
    // On enlève le point si le nombre devient un entier après l'arrondi
    if (formatted.endsWith(".")) {
        formatted = formatted.dropLast(1)
    }
    return formatted
}
