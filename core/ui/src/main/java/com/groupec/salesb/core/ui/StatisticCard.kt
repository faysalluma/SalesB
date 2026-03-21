package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatisticCard(
    modifier : Modifier = Modifier,
    labelRes: Int,
    labelText: String? = null,
    iconColor: Color = Primary,
    numberTitle: Int ? = null,
    dataValue: String ?,
    dataValueStyle: TextStyle = MaterialTheme.typography.titleLarge,
    devise: String ? = null,
    onclick: () -> Unit
) {
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
            modifier = Modifier.padding(16.dp).fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val cardLabel = labelText ?: stringResource(labelRes)
            val numberTitleValue = numberTitle?.let {
                "$cardLabel ($it)"
            } ?: cardLabel
            Text(
                numberTitleValue,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            FlowRow (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                val dataVal = devise?.let {
                    "$dataValue $it"
                } ?: dataValue
                Text(
                    dataVal.toString(),
                    style = dataValueStyle,
                    textAlign = TextAlign.Center,
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
    StatisticCard(labelRes = R.string.statistic_label_sale,
        numberTitle = 12, dataValue = "1000", devise = "EUR") {

    }
}
