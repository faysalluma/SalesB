package com.groupec.salesb.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppEditableExposedDropdown
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.IconTextButton
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
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.others.PaymentType
import com.groupec.salesb.core.model.data.others.paymentTypeFromLabel
import com.groupec.salesb.core.model.data.others.paymentTypeLabels
import com.groupec.salesb.core.normalizeDecimalSeparator
import com.groupec.salesb.core.print.PrintAction
import kotlinx.coroutines.launch


@Composable
fun SaleDetailCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: Map<Int, Boolean>,
    parameter: Parameter,
    isLoading: Boolean,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    onSave: suspend (Double, PrintAction) -> Boolean,
    onClear: () -> Unit,
    paymentTypeState: String,
    onPaymenTypeSelected: (String) -> Unit,
    clientItems: List<Pair<String, String>>,
    clientlibelleState: TextFieldValue,
    onClientlibelleState: (TextFieldValue) -> Unit,
    onClientSelected: (Pair<String, String>) -> Unit,
    navigateToClient: () -> Unit
) {
    val context = LocalContext.current
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceBusiness = parameter.isServiceBusiness,
        plural = true,
        capitalize = true
    )

    Column(
        modifier = modifier,
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
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .background(Silver)
                        .padding(8.dp)
                ) {
                    AppHeadLine(
                        leadingContent = {
                            TextNormal(
                                text = stringResource(R.string.title_product, catalogLabelPlural),
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

                SaleDetailList(
                    selectedProducts = selectedProducts,
                    textFieldValues = textFieldValues,
                    onQuantityChange = onQuantityChange,
                    parameter = parameter,
                    quantityCheck = quantityCheck
                )
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BottomContentScreen(
                    total = total,
                    parameter = parameter,
                    isLoading = isLoading,
                    enabled = enabled,
                    onSave = onSave,
                    onClear = onClear,
                    paymentTypeState = paymentTypeState,
                    onPaymenTypeSelected = onPaymenTypeSelected,
                    clientItems = clientItems,
                    clientlibelleState = clientlibelleState,
                    onClientlibelleState = onClientlibelleState,
                    onClientSelected = onClientSelected,
                    navigateToClient = navigateToClient
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BottomContentScreen(
    total: String,
    parameter: Parameter,
    isLoading: Boolean,
    onSave: suspend (Double, PrintAction) -> Boolean,
    onClear: () -> Unit,
    enabled: Boolean,
    paymentTypeState: String,
    onPaymenTypeSelected: (String) -> Unit,
    clientItems: List<Pair<String, String>>,
    clientlibelleState: TextFieldValue,
    onClientlibelleState: (TextFieldValue) -> Unit,
    onClientSelected: (Pair<String, String>) -> Unit,
    navigateToClient: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val showDialog = rememberSaveable { mutableStateOf(false) }
    var isCheckingPrinter by remember { mutableStateOf(false) }
    val totalLabel = total.toDouble().formatAmount().plus(" ${parameter.devise}")
    val paymentTypeList = paymentTypeLabels(context)
    var cashReceived by remember { mutableStateOf("") }
    val cashDue = ((cashReceived.toDoubleOrNull() ?: 0.0) - total.toDouble()).coerceAtLeast(0.0)
    var isClientLibelleError by remember { mutableStateOf(false) }

    val selectedPaymentType = paymentTypeFromLabel(context, paymentTypeState)
    val isCashSelected = selectedPaymentType == PaymentType.Cash
    val paddingBottom = 4.dp

    if (parameter.activeClient) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = paddingBottom),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.client_label),
                    modifier = Modifier.padding(top = 16.dp, end = 4.dp)
                )
                Row(modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable {
                        navigateToClient()
                    }
                ) {
                    Text("(")
                    Icon(
                        imageVector = AppIcons.Add,
                        contentDescription = stringResource(R.string.sale_add_client)
                    )
                    Text(")")
                }
            }

            AppEditableExposedDropdown(
                items = clientItems,
                label = stringResource(R.string.sale_client_label),
                isError = isClientLibelleError,
                supportingText = if (
                    clientlibelleState.text.isNotEmpty() && clientItems.none { it.second == clientlibelleState.text }
                ) {
                    {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(com.groupec.salesb.core.designsystem.R.string.invalid_select),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    null
                },
                value = clientlibelleState,
                onValueChange = {
                    onClientlibelleState(it)
                    if (isClientLibelleError) isClientLibelleError = false
                },
                modifier = Modifier.weight(1f).fillMaxWidth(),
                onItemSelected = { item ->
                    onClientSelected(item)
                    if (isClientLibelleError) isClientLibelleError = false
                }
            )
        }
    }

    // Payment mode
    if (parameter.activepaymentmode) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = paddingBottom),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.payment_type_label),
                modifier = Modifier.padding(top = 16.dp, end = 16.dp).weight(1f)
            )
            AppExposedDropdownMenu(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                items = paymentTypeList,
                value = paymentTypeState,
            ) { index, item ->
                onPaymenTypeSelected(item)
            }
        }
    }

    if (isCashSelected || parameter.devise.equals("fcfa", ignoreCase = true)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = paddingBottom),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.change_due, " $cashDue ${parameter.devise}"),
                modifier = Modifier.padding(top = 8.dp, end = 16.dp).weight(1f)
            )
            AppTextField(
                value = cashReceived,
                onChange = { data ->
                    cashReceived = data.normalizeDecimalSeparator()
                },
                label = stringResource(id = R.string.cash_received),
                placeholder = stringResource(
                    com.groupec.salesb.core.designsystem.R.string.enter_your_value,
                    stringResource(R.string.cash_received)
                ),
                fieldColor = White,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                fieldType = FieldType.Number
            )
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TitleMedium(
            modifier = Modifier.padding(top = 2.dp, end = 8.dp),
            title = stringResource(R.string.total)
        )
        Text(
            text = totalLabel,
            style = MaterialTheme.typography.titleLarge
        )
    }

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
            isClientLibelleError = clientlibelleState.text.isNotEmpty() &&
                parameter.activeClient &&
                clientItems.none { it.second == clientlibelleState.text }
            if (!isClientLibelleError) {
                showDialog.value = true
            }
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
                FlowRow (
                    horizontalArrangement = Arrangement.Center,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DefaultButton(
                        modifier = Modifier.wrapContentSize(),
                        containerColor = Silver,
                        border = BorderStroke(1.dp, Silver),
                        enabled = !isCheckingPrinter,
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
                    Spacer(modifier = Modifier.width(16.dp))

                    if (parameter.activeprinter) {
                        DefaultButton(
                            modifier = Modifier.wrapContentSize(),
                            text = stringResource(R.string.validate_and_print_receipt),
                            containerColor = LightGreen,
                            enabled = enabled && !isCheckingPrinter,
                            isLoading = isCheckingPrinter
                        ) {
                            scope.launch {
                                isCheckingPrinter = true
                                val shouldCloseDialog = onSave(total.toDouble(), PrintAction.Thermal)
                                isCheckingPrinter = false
                                if (shouldCloseDialog) {
                                    showDialog.value = false
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                    }

                    DefaultButton(
                        modifier = Modifier.wrapContentSize(),
                        text = stringResource(R.string.validate),
                        enabled = enabled && !isCheckingPrinter,
                    ) {
                        scope.launch {
                            val shouldCloseDialog = onSave(total.toDouble(), PrintAction.None)
                            if (shouldCloseDialog) {
                                showDialog.value = false
                            }
                        }
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
