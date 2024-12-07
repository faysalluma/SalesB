package com.groupec.salesb.core

import android.text.format.DateUtils
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Calendar.SUNDAY
import java.util.Date
import java.util.Locale

fun Date.isYesterday(): Boolean {
    return DateUtils.isToday(time + DateUtils.DAY_IN_MILLIS)
}

fun Date.isToday(): Boolean {
    return DateUtils.isToday(time)
}

fun Date.isTomorrow(): Boolean {
    return DateUtils.isToday(time - DateUtils.DAY_IN_MILLIS)
}

fun Date.isTodayOrTomorrow() = this.isToday() || this.isTomorrow()

fun Date.day(): String {
    return SimpleDateFormat("dd", Locale.getDefault()).format(this)
}

fun Date.month(): String {
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(this)
}

fun Date.year(): Int {
    return Calendar.getInstance().apply {
        setTime(timeInMillis)
    }.get(Calendar.YEAR)
}

fun Date.dayMonth(): String {
    return SimpleDateFormat("dd/MM", Locale.getDefault()).format(this)
}

fun Date.monthYear(): String {
    return SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(this)
}

fun Date.dayMonthYear(): String {
    return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(this)
}

/**
 * Formats a date using the standard DEFAULT/SHORT/MEDIUM/LONG/FULL formats available in [DateFormat].
 * Note that this will never include the time.
 *
 * Examples (Locale dependant, check https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html
 * for proper info)
 *
 * @sample toStandardDateString(DateFormat.DEFAULT): "30 juillet 2009"
 * @sample toStandardDateString(DateFormat.SHORT): "30/07/09"
 * @sample toStandardDateString(DateFormat.MEDIUM): "30 jui 2009"
 * @sample toStandardDateString(DateFormat.LONG): "30 juillet 2009"
 * @sample toStandardDateString(DateFormat.FULL): "mardi 30 juillet 2009"
 */
fun Date.toStandardDateString(format: Int): String {
    return DateFormat.getDateInstance(format, Locale.getDefault()).format(this)
}

fun Date.timeHourMinutes(): String {
    return SimpleDateFormat("HH':'mm", Locale.getDefault()).format(this)
}

fun Date.getNumberOfDaysSinceLastMonday(): Int {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = this.time
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)

    // Sunday is 1 since start of week in US System
    return when (dayOfWeek) {
        SUNDAY -> 6
        else -> dayOfWeek - 2
    }
}

fun Date.numberOfDaysInMonth(): Int {
    val cal = Calendar.getInstance(Locale.getDefault())
    cal.timeInMillis = this.time
    return cal.getActualMaximum(Calendar.DAY_OF_MONTH)
}

fun Date.toDateString(): String =
    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)

fun Date.sameDay(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    calendar.time = this
    val otherCalendar = Calendar.getInstance()
    otherCalendar.time = date

    return calendar.get(Calendar.DAY_OF_YEAR) == otherCalendar.get(Calendar.DAY_OF_YEAR) &&
            calendar.get(Calendar.YEAR) == otherCalendar.get(Calendar.YEAR)
}

fun String.toDate(format: String = "yyyy-mm-dd HH:mm:ss"): Date? {
    return try {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        dateFormat.parse(this)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun currentDateString() : String {
    val currentDateTime = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    return formatter.format(currentDateTime)
}

// Function to get the current date in yyyy-MM-dd format
fun getCurrentDate(): String {
    val currentDateTime = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(currentDateTime)
}

// Function to get yesterday's date in yyyy-MM-dd format
fun getYesterdayDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, -1) // Subtract 1 day
    val yesterdayDateTime = calendar.time
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(yesterdayDateTime)
}

fun getCurrentWeekDates(): List<String> {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Aller au premier jour de la semaine (lundi)
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

    val weekDates = mutableListOf<String>()

    // Ajouter chaque jour de la semaine en cours
    for (i in 0 until 7) {
        weekDates.add(formatter.format(calendar.time))
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Passer au jour suivant
    }

    return weekDates
}

fun getCurrentMonthDates(): List<String> {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Aller au premier jour du mois
    calendar.set(Calendar.DAY_OF_MONTH, 1)

    val monthDates = mutableListOf<String>()
    val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

    // Ajouter chaque jour du mois en cours
    for (i in 1..maxDay) {
        monthDates.add(formatter.format(calendar.time))
        calendar.add(Calendar.DAY_OF_YEAR, 1) // Passer au jour suivant
    }
    return monthDates
}

// Get start date and end date of a week
fun getCurrentWeekDelimitedDates(): Pair<String, String> {
    val calendar = Calendar.getInstance()

    // Récupérer le premier jour de la semaine (lundi)
    calendar.firstDayOfWeek = Calendar.MONDAY
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    val startOfWeek = calendar.time

    // Récupérer le dernier jour de la semaine (dimanche)
    calendar.add(Calendar.DAY_OF_WEEK, 6)
    val endOfWeek = calendar.time

    // Formatter les dates en chaîne
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val startDate = formatter.format(startOfWeek)
    val endDate = formatter.format(endOfWeek)

    return Pair(startDate, endDate)
}

// Get start date and end date of a month
fun getCurrentMontDelimitedDates(): Pair<String, String> {
    val calendar = Calendar.getInstance()

    // Récupérer le premier jour du mois
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val startOfMonth = calendar.time

    // Récupérer le dernier jour du mois
    calendar.add(Calendar.MONTH, 1) // Passer au mois suivant
    calendar.set(Calendar.DAY_OF_MONTH, 1) // Se positionner au début du mois suivant
    calendar.add(Calendar.DAY_OF_MONTH, -1) // Reculer d'un jour pour obtenir le dernier jour du mois courant
    val endOfMonth = calendar.time

    // Formatter les dates en chaîne
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val startDate = formatter.format(startOfMonth)
    val endDate = formatter.format(endOfMonth)

    return Pair(startDate, endDate)
}

