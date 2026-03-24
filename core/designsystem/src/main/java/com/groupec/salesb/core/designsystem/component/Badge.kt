package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
private fun AppBadge(text: String) {
    Text(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(Primary.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        text = text.uppercase(),
        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
        color = Primary
    )
}