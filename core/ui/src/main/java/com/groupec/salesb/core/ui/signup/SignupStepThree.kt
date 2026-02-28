package com.groupec.salesb.core.ui.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.HandleServiceOption
import com.groupec.salesb.core.designsystem.component.HandleServiceToggleType
import com.groupec.salesb.core.designsystem.component.SwitchRow
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.ui.R
import com.groupec.salesb.core.designsystem.R as Res


@Composable
fun SignupStepThree(
    showErrors: Boolean,
    state: SignupStepThreeFormState,
    onValueChange: (SignupStepThreeFormState) -> Unit
) {
    var wholeQuantities by rememberSaveable { mutableStateOf(true) }
    var productImages by rememberSaveable { mutableStateOf(true) }
    var showPaymentMode by rememberSaveable { mutableStateOf(false) }
    var printService by rememberSaveable { mutableStateOf(true) }

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
                onChange = { onValueChange(state.copy(devise = it)) },
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
                onChange = { onValueChange(state.copy(tva = it)) },
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
            checked = wholeQuantities,
            enabled = true,
            type = HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT
        ),
        onCheckedChanged = { wholeQuantities = it }
    )
    SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = Res.string.handle_service_show_product_images,
            descriptionRes = Res.string.handle_service_show_product_images_desc,
            checked = productImages,
            enabled = true,
            type = HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT
        ),
        onCheckedChanged = { productImages = it }
    )
    SwitchRow(
        verticalpadding = verticalpadding,
        option = HandleServiceOption(
            titleRes = Res.string.handle_service_payment_mode,
            descriptionRes = Res.string.handle_service_payment_mode_desc,
            checked = showPaymentMode,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PAYMENT_MODE
        ),
        onCheckedChanged = { showPaymentMode = it }
    )
}

data class SignupStepThreeFormState(
    val devise: String = "",
    val tva: String = "",
    val showInt: Int = 0,
    val showProductImage: Int = 0,
    val showPaymentMode: Int = 0,
    val activePrinter: Int = 0
) {
    fun isValid(): Boolean {
        return  devise.isNotBlank() &&  tva.isNotBlank()
    }
}