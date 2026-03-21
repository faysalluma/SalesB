package com.groupec.feature.handleservice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.HandleServiceOption
import com.groupec.salesb.core.designsystem.component.SwitchRow
import com.groupec.salesb.core.designsystem.component.HandleServiceToggleType
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.designsystem.R as Res

@Composable
fun HandleServiceScreen(
    viewModel: HandleServiceViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val options = listOf(
        HandleServiceOption(
            titleRes = Res.string.handle_service_view_service,
            descriptionRes = Res.string.handle_service_view_service_desc,
            checked = uiState.serviceView,
            enabled = true,
            type = HandleServiceToggleType.SERVICE_VIEW
        ),
        HandleServiceOption(
            titleRes = Res.string.handle_service_use_integer_price,
            descriptionRes = Res.string.handle_service_use_integer_price_desc,
            checked = uiState.useIntForPriceAndAmount,
            enabled = !uiState.isUseIntForPriceAndAmountDisabled,
            type = HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT
        ),
        HandleServiceOption(
            titleRes = if (uiState.serviceView) {
                Res.string.handle_service_show_service_images
            } else {
                Res.string.handle_service_show_product_images
            },
            descriptionRes = if (uiState.serviceView) {
                Res.string.handle_service_show_service_images_desc
            } else {
                Res.string.handle_service_show_product_images_desc
            },
            checked = uiState.showImageOnProduct,
            enabled = true,
            type = HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT
        ),
        HandleServiceOption(
            titleRes = Res.string.handle_service_payment_mode,
            descriptionRes = Res.string.handle_service_payment_mode_desc,
            checked = uiState.activePaymentMode,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_PAYMENT_MODE
        ),
        HandleServiceOption(
            titleRes = Res.string.handle_service_printer,
            descriptionRes = Res.string.handle_service_printer_desc,
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
                    SwitchRow(option = option, onCheckedChanged = { checked ->
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
