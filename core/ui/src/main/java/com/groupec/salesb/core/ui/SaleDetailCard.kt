package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.IconMinus
import com.groupec.salesb.core.designsystem.component.IconPlus
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.component.TitleSmall
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.normalizeDecimalSeparator
import ir.ehsannarmani.compose_charts.extensions.format


@Composable
fun SaleDetailCard(
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    devise: String,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    onSave: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleLarge(
            title = stringResource(R.string.summary),
            modifier = Modifier.padding(top = 8.dp, bottom = 22.dp)
        )

        if (selectedProducts.isEmpty()) {
            EmptyScreen()
        } else {
            // Top section
            Column(modifier = Modifier.weight(0.7f)) {
                Box(
                    modifier = Modifier
                        .background(Silver)
                        .padding(8.dp)
                ) {
                    AppHeadLine(
                        leadingContent = {
                            TextNormal(
                                text = stringResource(R.string.title_product),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W300)
                            )
                        },
                        text = stringResource(R.string.quantity),
                        trailingContent = {
                            TextNormal(
                                text = stringResource(R.string.price),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W300)
                            )
                        },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W300),
                    )
                }

                DetailSaleList(selectedProducts,textFieldValues, onQuantityChange, devise)
            }

            // Bottom section
            Column(
                modifier = Modifier.weight(0.3f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    val total = selectedProducts
                        .map { it.second.prixttc * textFieldValues[it.first]?.toDouble()!! }
                        .reduce { acc, value -> acc + value }
                        .autoRound()

                    TitleMedium(title = stringResource(R.string.total), modifier = Modifier.padding(top = 8.dp))
                    Text(
                        text = total.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    DefaultButton(
                        onClick = onSave,
                        text = stringResource(id = com.groupec.salesb.core.designsystem.R.string.btn_save),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun DetailSaleList(
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues:  MutableMap<Int, String>,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    devise: String
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
    ) {

        selectedProducts.forEach { productLine ->
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
                        value =  textFieldValues[index]!!,
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
    }
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun SaleCardPreview() {
    SalesBAppTheme {

    }
}
