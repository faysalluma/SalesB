package com.groupec.salesb.core

import android.content.Context
import com.groupec.salesb.common.R

enum class Period(val titleRes: Int, val startDate: String, val endDate: String = startDate) {
    Yesterday(R.string.yesterday, getYesterdayDate()),
    Today(R.string.today, getCurrentDate()),
    Week(R.string.week, getCurrentWeekDelimitedDates().first, getCurrentWeekDelimitedDates().second),
    Month(R.string.month, getCurrentMontDelimitedDates().first, getCurrentMontDelimitedDates().second);

    fun getTitle(context: Context) = context.getString(titleRes)
}

enum class PeriodDate(val titleRes: Int, val dates: List<String>) {
    Yesterday(R.string.yesterday, listOf(getYesterdayDate())),
    Today(R.string.today, listOf(getCurrentDate())),
    Week(R.string.week, getCurrentWeekDates()),
    Month(R.string.month, getCurrentMonthDates());

    fun getTitle(context: Context) = context.getString(titleRes)
}