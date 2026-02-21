package com.groupec.feature.handleservice

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.theme.White

private enum class HandleServiceToggleType {
    SERVICE_VIEW,
    SHOW_IMAGE_ON_PRODUCT,
    USE_INT_FOR_PRICE_AND_AMOUNT,
    ACTIVE_PAYMENT_MODE,
    ACTIVE_PRINTER
}

private data class HandleServiceOption(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val checked: Boolean,
    val enabled: Boolean,
    val type: HandleServiceToggleType
)

@Composable
fun HandleServiceScreen(
    viewModel: HandleServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val options = listOf(
        HandleServiceOption(
            titleRes = R.string.handle_service_view_service,
            descriptionRes = R.string.handle_service_view_service_desc,
            checked = uiState.serviceView,
            enabled = true,
            type = HandleServiceToggleType.SERVICE_VIEW
        ),
        HandleServiceOption(
            titleRes = R.string.handle_service_show_product_images,
            descriptionRes = R.string.handle_service_show_product_images_desc,
            checked = uiState.showImageOnProduct,
            enabled = true,
            type = HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT
        ),
        HandleServiceOption(
            titleRes = R.string.handle_service_use_integer_price,
            descriptionRes = R.string.handle_service_use_integer_price_desc,
            checked = uiState.useIntForPriceAndAmount,
            enabled = !uiState.isUseIntForPriceAndAmountDisabled,
            type = HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT
        ),
        HandleServiceOption(
            titleRes = R.string.handle_service_payment_mode,
            descriptionRes = R.string.handle_service_payment_mode_desc,
            checked = uiState.activePaymentMode,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PAYMENT_MODE
        ),
        HandleServiceOption(
            titleRes = R.string.handle_service_printer,
            descriptionRes = R.string.handle_service_printer_desc,
            checked = uiState.activePrinter,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PRINTER
        )
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = stringResource(R.string.handle_service_screen_title),
            style = MaterialTheme.typography.titleLarge
        )

        Card(
            colors = CardDefaults.cardColors(containerColor = White)
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                options.forEachIndexed { index, option ->
                    HandleServiceSwitchRow(option = option, onCheckedChanged = { checked ->
                        when (option.type) {
                            HandleServiceToggleType.SERVICE_VIEW -> viewModel.updateServiceView(checked)
                            HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT -> viewModel.updateShowImageOnProduct(checked)
                            HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT -> viewModel.updateUseIntForPriceAndAmount(checked)
                            HandleServiceToggleType.ACTIVE_PAYMENT_MODE -> viewModel.updateActivePaymentMode(checked)
                            HandleServiceToggleType.ACTIVE_PRINTER -> viewModel.updateActivePrinter(checked)
                        }
                    })

                    if (index < options.lastIndex) {
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun HandleServiceSwitchRow(
    option: HandleServiceOption,
    onCheckedChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(end = 16.dp)
    ) {
        Text(
            text = stringResource(option.titleRes),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(option.descriptionRes),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

        Switch(
            checked = option.checked,
            onCheckedChange = onCheckedChanged,
            enabled = option.enabled
        )
    }
}
