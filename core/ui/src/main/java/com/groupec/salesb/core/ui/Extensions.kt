package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float ? = null,
    alignment: TextAlign = TextAlign.Center,
    isTitle: Boolean
) {
    Text(
        text = text,
        Modifier
            .padding(10.dp)
            .then(if (weight != null) Modifier.weight(weight) else Modifier),
        fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
    )
}
fun String.truncate(maxLength: Int = 30): String {
    return if (this.length > maxLength) this.take(maxLength - 3) + "..." else this
}

