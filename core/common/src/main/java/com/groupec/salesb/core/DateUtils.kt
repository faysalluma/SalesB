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