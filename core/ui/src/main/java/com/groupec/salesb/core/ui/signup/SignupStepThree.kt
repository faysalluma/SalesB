package com.groupec.salesb.core.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.HandleServiceOption
import com.groupec.salesb.core.designsystem.component.HandleServiceToggleType
import com.groupec.salesb.core.designsystem.component.SwitchRow
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.others.paymentTypeLabels
import com.groupec.salesb.core.normalizeDecimalSeparator
import com.groupec.salesb.core.ui.R
import com.groupec.salesb.core.designsystem.R as Res


@Composable
fun SignupStepThree(
    showErrors: Boolean,
    isServiceBusiness: Boolean,
    state: SignupStepThreeFormState,
    paymentTypeState: String,
    onPaymenTypeSelected: (String) -> Unit,
    onValueChange: (SignupStepThreeFormState) -> Unit
) {
    val context = LocalContext.current
    val paymentTypeList = paymentTypeLabels(context)
    val maxLength = 5

    Text(
        stringResource(R.string.signup_step_3),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
    Text(
        stringResource(R.string.signup_subtitle_preferences),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge, color = Color.Gray,
        modifier = Modifier.padding(bottom = 18.dp)
    )

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = state.devise,
                onChange = { newValue ->
                    if (newValue.length <= maxLength) {
                        onValueChange(state.copy(devise = newValue))
                    }
                },
                label = stringResource(R.string.signup_currency),
                placeholder = stringResource(R.string.signup_currency_placeholder),
                isError = showErrors && state.devise.isBlank(),
                fieldColor = White,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = state.tva,
                onChange = {  newValue ->
                    if (newValue.length <= maxLength) {
                        onValueChange(state.copy(tva = newValue.normalizeDecimalSeparator()))
                    }
                },
                label = stringResource(R.string.signup_vat),
                placeholder = stringResource(R.string.signup_vat_placeholder),
                isError = showErrors && state.tva.isBlank(),
                fieldColor = White,
                modifier = Modifier.fillMaxWidth(),
                fieldType = FieldType.Number
            )
        }
    }

    Text(
        text = stringResource(R.string.signup_display_options),
        color = Color(0xFF8B9BB4),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp)
    )

    val verticalpadding = 12.dp
    SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = Res.string.handle_service_use_integer_price,
            descriptionRes = Res.string.handle_service_use_integer_price_desc,
            checked = state.showInt == 1,
            enabled = true,
            type = HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT
        ),
        onCheckedChanged = { checked ->
            onValueChange(state.copy(showInt = if (checked) 1 else 0))
        }
    )
    SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = if (isServiceBusiness) {
                Res.string.handle_service_show_service_images
            } else {
                Res.string.handle_service_show_product_images
            },
            descriptionRes = if (isServiceBusiness) {
                Res.string.handle_service_show_service_images_desc
            } else {
                Res.string.handle_service_show_product_images_desc
            },
            checked = state.showProductImage == 1,
            enabled = true,
            type = HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT
        ),
        onCheckedChanged = { checked ->
            onValueChange(state.copy(showProductImage = if (checked) 1 else 0))
        }
    )
    SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = Res.string.handle_service_payment_mode,
            descriptionRes = Res.string.handle_service_payment_mode_desc,
            checked = state.showPaymentMode == 1,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PAYMENT_MODE
        ),
        onCheckedChanged = { checked ->
            if (checked) {
                onValueChange(
                    state.copy(
                        showPaymentMode = 1,
                        defaultpayment = paymentTypeState
                    )
                )
            } else {
                onValueChange(
                    state.copy(
                        showPaymentMode = 0,
                        defaultpayment = ""
                    )
                )
            }
        }
    )

    if (state.showPaymentMode == 1) {
        AppExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            items = paymentTypeList,
            value = paymentTypeState,
            label = stringResource(R.string.payment_type_label),
            onValueChange = {
                onPaymenTypeSelected(it)
            }
        ) { _, item ->
            onValueChange(state.copy(defaultpayment = item))
        }
    }

    /*SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = Res.string.handle_service_printer,
            descriptionRes = Res.string.handle_service_printer_desc,
            checked = state.activePrinter == 1,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PRINTER
        ),
        onCheckedChanged = { checked ->
            onValueChange(state.copy(activePrinter = if (checked) 1 else 0))
        }
    )*/
}

data class SignupStepThreeFormState(
    val devise: String = "",
    val tva: String = "",
    val showInt: Int = 0,
    val showProductImage: Int = 1,
    val showPaymentMode: Int = 1,
    val defaultpayment: String = "",
    val activePrinter: Int = 0
) {
    fun isValid(): Boolean {
        return devise.isNotBlank() && tva.toDoubleOrNull() != null
    }
}
