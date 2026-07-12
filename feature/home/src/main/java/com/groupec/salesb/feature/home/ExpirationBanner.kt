package com.groupec.salesb.feature.home

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

internal enum class ExpirationBannerSeverity { WARNING, URGENT }

internal data class ExpirationBannerState(
    val daysRemaining: Long,
    val severity: ExpirationBannerSeverity,
)

private val supportedExpirationDateFormats = listOf(
    "yyyy-MM-dd",
    "yyyy-MM-dd'T'HH:mm:ss",
    "yyyy-MM-dd'T'HH:mm:ss.SSS",
    "dd/MM/yyyy",
)

internal fun expirationBannerState(
    expirationDate: String,
    now: Date = Date(),
): ExpirationBannerState? {
    val expiration = parseExpirationDate(expirationDate) ?: return null
    val daysRemaining = calendarDaysBetween(now, expiration)
    if (daysRemaining > 7) return null

    return ExpirationBannerState(
        daysRemaining = daysRemaining,
        severity = if (daysRemaining <= 3) {
            ExpirationBannerSeverity.URGENT
        } else {
            ExpirationBannerSeverity.WARNING
        },
    )
}

private fun parseExpirationDate(value: String): Date? {
    val trimmed = value.trim()
    val normalized = if (trimmed.length >= 10 && trimmed[4] == '-' && trimmed[7] == '-') {
        trimmed.take(10)
    } else {
        trimmed
    }
    return supportedExpirationDateFormats.firstNotNullOfOrNull { pattern ->
        val position = ParsePosition(0)
        SimpleDateFormat(pattern, Locale.ROOT).apply {
            isLenient = false
            timeZone = TimeZone.getDefault()
        }.parse(normalized, position)?.takeIf { position.index == normalized.length }
    }
}

private fun calendarDaysBetween(start: Date, end: Date): Long {
    fun Date.asUtcCalendarDate(): Long {
        val local = Calendar.getInstance().apply { time = this@asUtcCalendarDate }
        return Calendar.getInstance(TimeZone.getTimeZone("UTC")).run {
            clear()
            set(local.get(Calendar.YEAR), local.get(Calendar.MONTH), local.get(Calendar.DAY_OF_MONTH))
            timeInMillis
        }
    }

    return (end.asUtcCalendarDate() - start.asUtcCalendarDate()) / MILLIS_PER_DAY
}

private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

@Composable
internal fun ExpirationBanner(
    expirationDate: String,
    modifier: Modifier = Modifier,
) {
    val state = expirationBannerState(expirationDate) ?: return
    val background = when (state.severity) {
        ExpirationBannerSeverity.WARNING -> Color(0xFFF59E0B)
        ExpirationBannerSeverity.URGENT -> Color(0xFFC5192D)
    }

    Text(
        modifier = modifier
            .fillMaxWidth()
            .background(background)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        text = stringResource(state.messageRes(), state.daysRemaining.coerceAtLeast(0)),
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
    )
}

@StringRes
private fun ExpirationBannerState.messageRes(): Int = when {
    daysRemaining < 0 -> R.string.subscription_expired
    daysRemaining == 0L -> R.string.subscription_expires_today
    else -> R.string.subscription_expires_in_days
}
