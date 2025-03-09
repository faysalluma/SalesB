package com.groupec.salesb.core.ui

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Product


@Composable
fun SaleDetailCard(
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: Map<Int, Boolean>,
    devise: String,
    isLoading: Boolean,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    onSave: (Double) -> Unit,
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
    onSave: (Double) -> Unit,
    onClear: () -> Unit,
    enabled: Boolean
) {
    val focusManager = LocalFocusManager.current
    val showDialog = rememberSaveable { mutableStateOf(false) }
    val totalLabel = total.plus(" $devise")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TitleMedium(title = stringResource(R.string.total), modifier = Modifier.padding(top = 8.dp))
        Text(
            text = total,
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
        AppAlertInfoDialog(
            setShowDialog = {
                showDialog.value = it
                focusManager.clearFocus()
            },
            title = stringResource(R.string.confirm_sale_message, totalLabel),
            isLoading = isLoading,
            onConfirmButton = {
                onSave(total.toDouble())
            },
            disableConfirmActionDismiss = true,
            onDismissButton = {
                focusManager.clearFocus()
            }
        )
    }
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun SaleDetailCardPreview() {
    SalesBAppTheme {

    }
}
