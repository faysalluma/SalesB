package com.groupec.salesb.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.IconTextButton
import com.groupec.salesb.core.designsystem.component.Position
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleHeader
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.LightGreen
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.print.PrintAction


@Composable
fun SaleDetailCard(
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: Map<Int, Boolean>,
    devise: String,
    isLoading: Boolean,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    onSave: (Double, PrintAction) -> Unit,
    onClear: () -> Unit
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

                SaleDetailList(selectedProducts, textFieldValues, onQuantityChange, devise, quantityCheck)
            }

            // Bottom section
            val total = selectedProducts
                .map {
                    it.second.prixttc * textFieldValues[it.first]?.toDouble()!!
                }
                .reduce { acc, value -> acc + value }
                .autoRound()

            val enabled = selectedProducts.all { quantityCheck[it.first] == false }

            Column(
                modifier = Modifier.weight(0.3f),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                BottomContentScreen(
                    total = total,
                    devise = devise,
                    isLoading = isLoading,
                    enabled = enabled,
                    onSave = onSave,
                    onClear = onClear
                )
            }
        }
    }
}

@Composable
private fun BottomContentScreen(
    total: String,
    devise: String,
    isLoading: Boolean,
    onSave: (Double, PrintAction) -> Unit,
    onClear: () -> Unit,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val totalLabel = total.toDouble().formatAmount().plus(" $devise")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TitleMedium(title = stringResource(R.string.total), modifier = Modifier.padding(top = 8.dp))
        Text(
            text = totalLabel,
            style = MaterialTheme.typography.titleLarge
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        DefaultButton(
            modifier = Modifier.weight(1f),
            text = stringResource(id = com.groupec.salesb.core.designsystem.R.string.btn_save),
            enabled = enabled,
            style = MaterialTheme.typography.titleMedium
        ) {
            showDialog.value = true
        }
        Spacer(Modifier.width(16.dp))
        DefaultButton(
            modifier = Modifier.weight(1f),
            containerColor = Silver,
            border = BorderStroke(1.dp, Silver),
            onClick = onClear,
            text = stringResource(id = com.groupec.salesb.core.designsystem.R.string.btn_cancel),
            style = MaterialTheme.typography.titleMedium.copy(
                color = Black,
                fontWeight = FontWeight.W400
            )
        )
    }

    // Show Dialogue
    if (showDialog.value) {
        AppCustomDialog(setShowDialog = {
            focusManager.clearFocus()
            showDialog.value = it
        } ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TitleHeader(
                    title = stringResource(R.string.confirm_sale_message, totalLabel),
                    color = Color.Unspecified,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DefaultButton(
                        modifier = Modifier.wrapContentSize(),
                        containerColor = Silver,
                        border = BorderStroke(1.dp, Silver),
                        textcolor = Color.Black,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Black,
                            fontWeight = FontWeight.W400
                        ),
                        text = stringResource(R.string.btn_cancel)
                    ) {
                        showDialog.value = false
                        focusManager.clearFocus()
                    }

                    Box {
                        val spacer = 10.dp
                        IconTextButton(
                            text = stringResource(R.string.validate_and_edit_invoice),
                            position = Position.Right,
                            contentPadding = PaddingValues(16.dp),
                            icon = {
                                Icon(imageVector = AppIcons.ChevronDown, contentDescription = "Arrow down")
                            }
                        ) {
                            expanded = true
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(White)
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.validate_and_print_receipt)) },
                                onClick = {
                                    onSave(total.toDouble(), PrintAction.Thermal)
                                    // expanded = false // Close DropdownMenuItem

                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = spacer))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.validate_and_print_invoice_a4)) },
                                onClick = {
                                    onSave(total.toDouble(), PrintAction.Normal)
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = spacer))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.validate_and_send_invoice)) },
                                onClick = {
                                    onSave(total.toDouble(), PrintAction.SendByEmail)
                                }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = spacer))
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.validate_and_download_invoice)) },
                                onClick = {
                                    onSave(total.toDouble(), PrintAction.Download)
                                    expanded = false // Close DropdownMenuItem
                                }
                            )
                        }
                    }

                    DefaultButton(
                        modifier = Modifier.wrapContentSize(),
                        text = stringResource(R.string.validate),
                        containerColor = LightGreen
                    ) {
                        onSave(total.toDouble(), PrintAction.None)
                        showDialog.value = false
                    }
                }
            }
        }
    }
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun SaleDetailCardPreview() {
    SalesBAppTheme {

    }
}
