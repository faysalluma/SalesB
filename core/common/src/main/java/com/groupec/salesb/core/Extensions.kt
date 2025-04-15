package com.groupec.salesb.core

import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale

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

fun String.convertToServerDateFormat(): String {
    val locale = Locale.getDefault()
    val pattern = if (locale.language.equals("fr", ignoreCase = true)) {
        "dd/MM/yyyy"
    } else {
        "MM/dd/yyyy"
    }

    val originalFormat = SimpleDateFormat(pattern, locale)
    val targetFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    val date = originalFormat.parse(this)

    // Formater l'objet Date dans le format souhaité
    return targetFormat.format(date)
}

fun String.convertToViewDateFormat(): String {
    val locale = Locale.getDefault()
    val originalFormat = SimpleDateFormat("yyyy-MM-dd", locale)
    val targetPattern = if (locale.language.equals("fr", ignoreCase = true)) {
        "dd/MM/yyyy"
    } else {
        "MM/dd/yyyy"
    }
    val targetFormat = SimpleDateFormat(targetPattern, locale)
    val date = originalFormat.parse(this)

    // Formater l'objet Date dans le format souhaité
    return targetFormat.format(date)
}

// Because of BCrypt in Java/Kotlin (for org.mindrot.BCrypt) don't accept $2y$ format
fun String.fixBCryptHash(): String {
    return this.replace("$2y$", "$2a$")
}



