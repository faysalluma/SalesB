package com.groupec.salesb.core

import android.content.Context
import com.groupec.salesb.common.R

enum class Period(val titleRes: Int) {
    Today(R.string.today),
    Yesterday(R.string.yesterday),
    Week(R.string.week),
    Month(R.string.month);

    fun getTitle(context: Context) = context.getString(titleRes)
}