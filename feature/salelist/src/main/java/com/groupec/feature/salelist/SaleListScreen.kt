package com.groupec.feature.salelist

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.convertToServerDateFormat
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.designsystem.component.AppCustomBottomSheet
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DatePickerFieldToModal
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.print.PrintAction
import com.groupec.salesb.core.ui.InvoiceAction
import com.groupec.salesb.core.ui.InvoiceContent
import com.groupec.salesb.core.ui.InvoicingInfoScreen
import com.groupec.salesb.core.ui.SaleCardList
import com.groupec.salesb.core.ui.SaleItemDetailProduct

@OptIn(ExperimentalPermissionsApi::class, ExperimentalComposeUiApi::class,
    ExperimentalComposeApi::class, ExperimentalLayoutApi::class
)
@Composable
fun SaleListScreen(
    snackbarHostState: SnackbarHostState,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
    navigateToSaleChart: (String, String) -> Unit,
    viewModel: SaleListViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val sales = viewModel.pagedProducts.collectAsLazyPagingItems()
    val error = (sales.loadState.refresh as? LoadState.Error)?.error?.message
    val parameter by viewModel.parameter.collectAsStateWithLifecycle()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showInvoiceDialog by rememberSaveable { mutableStateOf(false) }
    var saleGetValue by rememberSaveable { mutableStateOf<Sale?>(null) }
    var invoicingGetValue by rememberSaveable { mutableStateOf<Invoicing?>(null) }
    var startDate by rememberSaveable { mutableStateOf(currentLocalDateString()) }
    var endDate by rememberSaveable { mutableStateOf(currentLocalDateString()) }
    val thermalPrintUiSate by viewModel.printUiState.collectAsState(FormUIState.Idle)
    var showInvoice by rememberSaveable { mutableStateOf(false) }
    var sendByEmail by rememberSaveable { mutableStateOf(false) }
    val saveReceiptToDownloadsState by viewModel.saveReceiptToDownloads.collectAsStateWithLifecycle()
    var showBottomSheet by remember { mutableStateOf(false) }

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("bluetoothLauncher", "Success")
            saleGetValue?.let { sale ->
                viewModel.printThermalReceipt(
                    sale = sale,
                    parameter = parameter
                )
            }
        } else {
            Log.w("bluetoothLauncher", "Failed")
        }
    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    val bluetoothManager = remember { context.getSystemService(BluetoothManager::class.java) }
    val bluetoothAdapter: BluetoothAdapter? = remember { bluetoothManager.adapter }

    // When save to Downloads notify user
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) {
            viewModel.savePdfToDownloads(context, saleGetValue!!, parameter, invoicingGetValue!!)
        } else {
            Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    when (saveReceiptToDownloadsState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                val file = (saveReceiptToDownloadsState as FormUIState.Success).data
                viewModel.showDownloadNotification(context, file)
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(R.string.donwload_completed_and_save)
                    )
                )
            }
        }
        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(saveReceiptToDownloadsState as FormUIState.Error).message,
                        isError = true
                    )
                )
            }
        }

        else -> {}
    }

    when (thermalPrintUiSate) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(R.string.print_succesfully)
                    )
                )
            }
        }
        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                val message =(thermalPrintUiSate as FormUIState.Error).message
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
        else -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 12.dp, top = 6.dp)
    ) {
        // if get error when fetching products
        if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorScreen(
                    error = error,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 16.dp)
                )
                DefaultButton(
                    modifier = Modifier.wrapContentWidth(),
                    text = stringResource(R.string.retry)
                ) {
                    sales.refresh()
                }
            }
        } else {
            // Show Progress bar waiting load products
            if (!isSearching && sales.itemCount == 0) {
                AppLoadingScreen(text = stringResource(R.string.loading_sales))
            } else {
                Column {
                    if (isExpandedWidth) {
                        // Head
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(22.dp)
                        ) {

                            FlowRow(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                TitleLarge(
                                    title = stringResource(R.string.my_sales),
                                    modifier = Modifier.padding(top = 22.dp)
                                )

                                // Barre de recherche
                                AppTextField(
                                    value = searchQuery,
                                    leadingIcon = {
                                        Icon(
                                            imageVector = AppIcons.Search,
                                            contentDescription = "Search icon"
                                        )
                                    },
                                    trailingIcon = {
                                        if (searchQuery.isNotEmpty()) {
                                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                Icon(
                                                    imageVector = AppIcons.Close,
                                                    contentDescription = "Clear text"
                                                )
                                            }
                                        }
                                    },
                                    onChange = { viewModel.updateSearchQuery(it) },
                                    placeholder = stringResource(R.string.search_sale_place_holder),
                                    fieldType = FieldType.Text,
                                    fieldColor = Silver,
                                    modifier = Modifier.padding(top = 8.dp),
                                    shape = RoundedCornerShape(26.dp)
                                )
                            }

                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                verticalArrangement = Arrangement.spacedBy(22.dp),
                            ) {
                                DatePickerFieldToModal(
                                    modifier = Modifier
                                        .width(230.dp),
                                    label = stringResource(R.string.start_date),
                                    defaultDate = startDate
                                ) { dateValue ->
                                    viewModel.updateStartDateQuery(dateValue.convertToServerDateFormat())
                                    startDate = dateValue
                                }

                                DatePickerFieldToModal(
                                    modifier = Modifier
                                        .width(230.dp),
                                    label = stringResource(R.string.end_date),
                                    defaultDate = endDate
                                ) { dateValue ->
                                    viewModel.updateEndDateQuery(dateValue.convertToServerDateFormat())
                                    endDate = dateValue
                                }

                                DefaultButton(
                                    modifier = Modifier
                                        .wrapContentWidth()
                                        .padding(top = 10.dp),
                                    text = stringResource(R.string.view_chart),
                                    onClick = {
                                        navigateToSaleChart(
                                            startDate.convertToServerDateFormat(),
                                            endDate.convertToServerDateFormat()
                                        )
                                    }
                                )
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(top = 30.dp))
                    } else {
                        FlowRow(
                            modifier = modifier.fillMaxWidth().padding(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TitleLarge(
                                title = stringResource(R.string.my_sales),
                                modifier = Modifier.padding(top = 16.dp)
                            )

                            Row {
                                DefaultButton(
                                    modifier = Modifier.wrapContentWidth().padding(end = 16.dp),
                                    text = stringResource(R.string.filter),
                                    containerColor = Green,
                                    textcolor = White
                                ) {
                                    showBottomSheet = !showBottomSheet
                                }

                                DefaultButton(
                                    modifier = Modifier.wrapContentWidth(),
                                    text = stringResource(R.string.view_chart),
                                    onClick = {
                                        navigateToSaleChart(
                                            startDate.convertToServerDateFormat(),
                                            endDate.convertToServerDateFormat()
                                        )
                                    }
                                )
                            }
                        }

                        if (showBottomSheet) {
                            AppCustomBottomSheet(
                                onDismiss = {
                                    showBottomSheet = false
                                },
                                header = stringResource(R.string.select)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    // Barre de recherche
                                    AppTextField(
                                        value = searchQuery,
                                        leadingIcon = {
                                            Icon(
                                                imageVector = AppIcons.Search,
                                                contentDescription = "Search icon"
                                            )
                                        },
                                        trailingIcon = {
                                            if (searchQuery.isNotEmpty()) {
                                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                                    Icon(
                                                        imageVector = AppIcons.Close,
                                                        contentDescription = "Clear text"
                                                    )
                                                }
                                            }
                                        },
                                        onChange = { viewModel.updateSearchQuery(it) },
                                        placeholder = stringResource(R.string.search_sale_place_holder),
                                        fieldType = FieldType.Text,
                                        fieldColor = Silver,
                                        modifier = Modifier.padding(top = 8.dp),
                                        shape = RoundedCornerShape(26.dp)
                                    )

                                    Spacer(Modifier.height(16.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                                        verticalArrangement = Arrangement.spacedBy(22.dp),
                                    ) {
                                        DatePickerFieldToModal(
                                            modifier = Modifier
                                               ,
                                            label = stringResource(R.string.start_date),
                                            defaultDate = startDate
                                        ) { dateValue ->
                                            viewModel.updateStartDateQuery(dateValue.convertToServerDateFormat())
                                            startDate = dateValue
                                        }

                                        DatePickerFieldToModal(
                                            modifier = Modifier
                                                ,
                                            label = stringResource(R.string.end_date),
                                            defaultDate = endDate
                                        ) { dateValue ->
                                            viewModel.updateEndDateQuery(dateValue.convertToServerDateFormat())
                                            endDate = dateValue
                                        }
                                    }

                                    Spacer(Modifier.height(22.dp))
                                }
                            }
                        }
                    }


                    // Paginated list
                    if (sales.itemCount == 0) {
                        EmptyScreen(text = stringResource(R.string.no_sales))
                    } else {
                        SaleCardList(
                            sales = sales,
                            isSearching = isSearching,
                            onViewDetail = { sale ->
                                saleGetValue = sale
                                showDialog = true
                            },
                            onPrintOrShare = { sale, printAction ->
                                saleGetValue = sale
                                when (printAction) {
                                    PrintAction.Normal -> {
                                        showInvoiceDialog = true
                                        sendByEmail = false
                                    }
                                    PrintAction.Thermal -> {
                                        if (bluetoothPermissions.allPermissionsGranted) {
                                            if (bluetoothAdapter?.isEnabled == true) {
                                                // Bluetooth is on print the receipt
                                                viewModel.printThermalReceipt(
                                                    sale = sale,
                                                    parameter = parameter
                                                )
                                            } else {
                                                // Bluetooth is off, ask user to turn it on
                                                enableBluetoothContract.launch(enableBluetoothIntent)
                                            }
                                        } else {
                                            bluetoothPermissions.launchMultiplePermissionRequest()
                                            // Show error message
                                            Toast.makeText(context,"Permission denied for access bluetooth", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                    PrintAction.SendByEmail -> {
                                        showInvoiceDialog = true
                                        sendByEmail = true // Notify to send by email operation (show email field)
                                        showInvoice = false // Re-open form dialog
                                    }

                                    else -> {}
                                }
                            }
                        )
                    }

                    if (showDialog) {
                        AppCustomDialog(setShowDialog = { showDialog = it} ) {
                            SaleItemDetailProduct(sale = saleGetValue, devise = parameter.devise)
                        }
                    }

                    if (showInvoiceDialog) {
                        AppCustomDialog(setShowDialog = { showInvoiceDialog = it} ) {
                            if (!showInvoice) {
                                InvoicingInfoScreen(sendByEmail = sendByEmail) { invoicingData ->
                                    invoicingGetValue = invoicingData
                                    if (sendByEmail && saleGetValue != null) {
                                        viewModel.sendByEmail(
                                            activityContext = context,
                                            sale = saleGetValue!!,
                                            parameter = parameter,
                                            invoicing = invoicingData
                                        )
                                        showInvoiceDialog = false
                                    } else if (!sendByEmail) {
                                        showInvoice = true // Show Invoice content that will be printed
                                    }
                                }
                            } else {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    /* Print Screen for pdf */
                                    // Composable content to be captured.
                                    // Here, everything inside below Column will be get captured
                                    if (saleGetValue != null && invoicingGetValue != null) {

                                        InvoiceAction(
                                            onChangeInvoiceData = {
                                                showInvoice = false
                                            },
                                            onDownload = {
                                                showInvoiceDialog = false
                                                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                                                    launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                } else {
                                                    viewModel.savePdfToDownloads(context, saleGetValue!!, parameter, invoicingGetValue!!)
                                                }
                                            },
                                            onPrint = {
                                                viewModel.onPrint(context, saleGetValue!!, parameter, invoicingGetValue!!)
                                            },
                                        )

                                        InvoiceContent(
                                            sale = saleGetValue!!,
                                            parameter = parameter,
                                            invoicing = invoicingGetValue!!
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
