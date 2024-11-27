package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun StatisticCard(
    modifier : Modifier = Modifier,
    labelRes: Int,
    iconColor: Color = Primary,
    value: Float, onclick: () -> Unit
) {
    val context = LocalContext.current
    val labelValue = context.getString(labelRes)
    Card(
        modifier = modifier,
            // .fillMaxWidth()
            // .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp), // Ombre de la carte
        shape = RoundedCornerShape(8.dp),  // Bords arrondis
        colors = CardDefaults.cardColors(
            containerColor = White
        ),
        onClick = onclick
    ) {
        // Content of the card
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                stringResource(R.string.statistic_label_title, labelValue),
                fontWeight = FontWeight.Bold
            )
            Row {
                Text(
                    value.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Icon(
                    imageVector = AppIcons.CheckCircle,
                    tint = iconColor,
                    contentDescription = "Check Circle"
                )
            }
        }
    }
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun StatisticCardPreview() {
    StatisticCard(labelRes = R.string.statistic_label_sale, value = 100f) {

    }
}