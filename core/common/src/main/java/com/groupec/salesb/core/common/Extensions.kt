package com.groupec.salesb.core

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ibm.icu.text.RuleBasedNumberFormat
import java.io.File
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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

// Formatter pour afficher le montant en chaîne de caractères avec deux décimales, selon la locale par défaut du système
// En français, le séparateur de milliers est un espace, remplace l'espace par point si forceStyleFrenchUseDot
fun Double.formatAmount(forceStyleFrenchUseDot: Boolean = false): String {
    val locale = Locale.getDefault()
    val symbols = DecimalFormatSymbols(locale)

    val decimalFormat = DecimalFormat("#,##0.00", symbols)
    var formatted = decimalFormat.format(this)

    if (forceStyleFrenchUseDot && locale.language == "fr") {
        // Remplacer les espaces insécables (U+00A0 ou U+202F) par des points
        formatted = formatted.replace(Regex("[\\u00A0\\u202F]"), ".")
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

fun getDrawableResIdIfExists(context: Context, drawableName: String = "logo"): Int? {
    val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
    return resId.takeIf { it != 0 }
}

fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    val drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!

    val bitmap = createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun Double.toPercentFormat() = "${(this * 100).toInt()} %"

fun Double.toWordsWithIcuRespectingFrOrEn(): String {
    val locale = if (Locale.getDefault().language == "fr") Locale.FRENCH else Locale.ENGLISH
    val formatter = RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT)

    val euros = this.toInt()
    val cents = ((this - euros) * 100).toInt()

    val euroLabel = if (locale.language == "fr") " euro" else " euro"
    val centLabel = if (locale.language == "fr") " centime" else " cent"

    val euroPart = formatter.format(euros) + euroLabel + if (euros > 1 && locale.language == "fr") "s" else ""
    val centPart = if (cents > 0) {
        val plural = if (cents > 1) "s" else ""
        " and ${formatter.format(cents)}$centLabel$plural"
    } else ""

    return euroPart + centPart
}

fun Double.toWordsWithIcuRespectingLocaleAndCurrency(
    mainUnit: String = "euro",
    subUnit: String? = "centime" // null si la devise n’a pas de sous-unité
): String {
    val locale = if (Locale.getDefault().language == "fr") Locale.FRENCH else Locale.ENGLISH
    val formatter = RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT)

    val mainValue = this.toInt()
    val subValue = ((this - mainValue) * 100).toInt()

    val mainLabel = " $mainUnit" + if (locale.language == "fr" && mainValue > 1) "s" else ""
    val mainPart = formatter.format(mainValue) + mainLabel

    val subPart = if (subUnit != null && subValue > 0) {
        val subLabel = " $subUnit" + if (locale.language == "fr" && subValue > 1) "s" else ""
        (if (locale.language == "fr") " et " else " and ") + formatter.format(subValue) + subLabel
    } else ""

    return mainPart + subPart
}

fun Context.sendEmailWithAttachment(
    addresses: Array<String> = arrayOf(),
    subject: String,
    body: String = "",
    attachment: File ? = null
) {
    val uri = attachment?.let {
        FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            it
        )
    }

    /*
    val shareIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:") // Only email apps handle this (and dont support file send).
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }*/

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/pdf"
        putExtra(Intent.EXTRA_EMAIL, addresses)
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
        uri?.let {
            putExtra(Intent.EXTRA_STREAM, it)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    startActivity(Intent.createChooser(shareIntent , "Send with:"))
}


