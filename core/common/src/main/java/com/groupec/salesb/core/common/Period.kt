package com.groupec.salesb.core

import android.content.Context

enum class Period(val titleRes: Int) {
    Yesterday(R.string.yesterday),
    Today(R.string.today),
    Week(R.string.week),
    Month(R.string.month);

    fun dateRange(): Pair<String, String> = when (this) {
        Yesterday -> getYesterdayDate().let { it to it }
        Today -> getCurrentDate().let { it to it }
        Week -> getCurrentWeekDelimitedDates()
        Month -> getCurrentMontDelimitedDates()
    }

    val startDate: String
        get() = dateRange().first

    val endDate: String
        get() = dateRange().second

    fun getTitle(context: Context) = context.getString(titleRes)
}

enum class PeriodDate(val titleRes: Int, val dates: List<String>) {
    Yesterday(R.string.yesterday, listOf(getYesterdayDate())),
    Today(R.string.today, listOf(getCurrentDate())),
    Week(R.string.week, getCurrentWeekDates()),
    Month(R.string.month, getCurrentMonthDates());

    fun getTitle(context: Context) = context.getString(titleRes)
}
