package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.IconMinus
import com.groupec.salesb.core.designsystem.component.IconPlus
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleSmall
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.normalizeDecimalSeparator


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SaleDetailListItem(
    productLine: Pair<Int, Product>,
    textFieldValues: MutableMap<Int, String>,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    devise: String
) {
    val (index, product) = productLine

    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 6.dp),
    ) {
        TitleSmall(
            title = product.libelle,
            modifier = Modifier.weight(1.2f)
        )
        Row(
            modifier = Modifier.weight(2f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconMinus {
                textFieldValues[index] = textFieldValues[index]!!.toDouble().minus(1.0).autoRound()
                onQuantityChange(Pair(index, product))
            }
            TextField(
                value = textFieldValues[index]!!,
                onValueChange = {
                    // textFieldQuantity = it.normalizeDecimalSeparator()
                    textFieldValues[index] = it.normalizeDecimalSeparator()
                    onQuantityChange(Pair(index, product))
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(
                    focusedContainerColor = Silver,
                    unfocusedContainerColor = Silver,
                    focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                    unfocusedIndicatorColor = Color.Transparent // Remove underline when unfocused
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                singleLine = true,
                textStyle = TextStyle(
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp // Optional: customize text size
                ),
                modifier = Modifier
                    .scale(0.9f)
                    .width(75.dp)
            )
            IconPlus {
                textFieldValues[index] = textFieldValues[index]!!.toDouble().plus(1.0).autoRound()
                onQuantityChange(Pair(index, product))
            }
        }
        Column(modifier = Modifier.weight(0.8f), horizontalAlignment = Alignment.End) {
            Text(
                text = product.prixttc.toString(),
                fontWeight = FontWeight.W500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            TextNormal(
                text = devise,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.W300)
            )
        }
    }
    HorizontalDivider()
}