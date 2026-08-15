package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme

@Composable
fun EmptyScreen(text : String ? = null, modifier: Modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text ?: stringResource(R.string.no_data),
            style = MaterialTheme.typography.bodyLarge.copy(textAlign = TextAlign.Center)
        )
    }
}

@Composable
@Preview
fun EmptyScreenPreview() {
    SalesBAppTheme {
        EmptyScreen()
    }
}