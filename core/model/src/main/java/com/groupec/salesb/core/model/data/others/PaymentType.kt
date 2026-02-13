package com.groupec.salesb.core.model.data.others

import android.content.Context
import com.groupec.salesb.core.model.R

enum class PaymentType(val libelleRes: Int) {
    Card(R.string.card),
    Cash(R.string.cash),
    Check(R.string.cheque)
}

fun paymentTypeLabels(context: Context): List<String> = PaymentType.entries.map { context.getString(it.libelleRes) }

fun paymentTypeFromLabel(context: Context, label: String): PaymentType? {
    return PaymentType.entries.firstOrNull { context.getString(it.libelleRes) == label }
}

fun paymentTypeValue(paymentType: PaymentType): String = paymentType.name.lowercase()

fun paymentTypeFromValue(value: String): PaymentType? {
    return PaymentType.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
}

fun paymentTypeLibelleResFromValue(value: String): Int? {
    return paymentTypeFromValue(value)?.libelleRes
}
