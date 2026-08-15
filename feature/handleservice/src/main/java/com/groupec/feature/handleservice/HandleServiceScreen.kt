package com.groupec.feature.handleservice

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
    val context = LocalContext.current
    val activity = context.findActivity()
    val uiState by viewModel.uiState.collectAsState()
    var bluetoothPermissionRequested by rememberSaveable { mutableStateOf(false) }
    val bluetoothPermissions = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
            )
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH,
                Manifest.permission.BLUETOOTH_ADMIN,
            )
        }
    }

    val bluetoothPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = bluetoothPermissions.all { permission ->
            permissions[permission] == true ||
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
        viewModel.updateActivePrinter(allGranted)
    }

    fun hasAllBluetoothPermissions(): Boolean {
        return bluetoothPermissions.all { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun shouldOpenBluetoothSettings(): Boolean {
        if (!bluetoothPermissionRequested || activity == null) return false

        return bluetoothPermissions.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED &&
                !ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)
        }
    }

    fun openAppSettings() {
        context.startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        )
    }

    val options = listOf(
        HandleServiceOption(
            titleRes = Res.string.handle_service_use_integer_price,
            descriptionRes = Res.string.handle_service_use_integer_price_desc,
            checked = uiState.useIntForPriceAndAmount,
            enabled = true,
            type = HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT
        ),
        HandleServiceOption(
            titleRes = if (uiState.isServiceBusiness) {
                Res.string.handle_service_show_service_images
            } else {
                Res.string.handle_service_show_product_images
            },
            descriptionRes = if (uiState.isServiceBusiness) {
                Res.string.handle_service_show_service_images_desc
            } else {
                Res.string.handle_service_show_product_images_desc
            },
            checked = uiState.showImageOnProduct,
            enabled = true,
            type = HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT
        ),
        HandleServiceOption(
            titleRes = Res.string.handle_service_client,
            descriptionRes = Res.string.handle_service_client_desc,
            checked = uiState.activeClient,
            enabled = true,
            type = HandleServiceToggleType.ACTIVE_CLIENT
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
                            HandleServiceToggleType.SHOW_IMAGE_ON_PRODUCT -> viewModel.updateShowImageOnProduct(checked)
                            HandleServiceToggleType.USE_INT_FOR_PRICE_AND_AMOUNT -> viewModel.updateUseIntForPriceAndAmount(checked)
                            HandleServiceToggleType.ACTIVE_CLIENT -> viewModel.updateActiveClient(checked)
                            HandleServiceToggleType.ACTIVE_PAYMENT_MODE -> viewModel.updateActivePaymentMode(checked)
                            HandleServiceToggleType.ACTIVE_PRINTER -> {
                                if (!checked) {
                                    viewModel.updateActivePrinter(false)
                                } else {
                                    if (hasAllBluetoothPermissions()) {
                                        viewModel.updateActivePrinter(true)
                                    } else if (shouldOpenBluetoothSettings()) {
                                        viewModel.updateActivePrinter(false)
                                        openAppSettings()
                                    } else {
                                        bluetoothPermissionRequested = true
                                        bluetoothPermissionLauncher.launch(bluetoothPermissions)
                                    }
                                }
                            }
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

private tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
