package com.groupec.salesb.core.ui

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
import com.groupec.salesb.core.ExpirationBannerSeverity
import com.groupec.salesb.core.ExpirationBannerState
import com.groupec.salesb.core.expirationBannerState

@Composable
fun ExpirationBanner(
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
