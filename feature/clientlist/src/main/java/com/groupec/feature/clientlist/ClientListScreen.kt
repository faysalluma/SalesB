package com.groupec.feature.clientlist

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.salesb.core.ExportType
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.showDownloadNotification
import com.groupec.salesb.core.ui.ClientCardList
import com.groupec.salesb.core.ui.ProFeatureBottomSheet

@Composable
fun ClientListScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: ClientListViewModel = hiltViewModel(),
    refreshClientList: Boolean,
    removeSelectedBgColor: Boolean,
    fromDetail: Boolean = false,
    onNavigateToSubscription: () -> Unit,
    onViewDetail: (Client) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val totalClientsCount by viewModel.totalClientsCount.collectAsState()
    val clients = viewModel.pagedClients.collectAsLazyPagingItems()
    val error = (clients.loadState.refresh as? LoadState.Error)?.error?.message
    val deleteClientState by viewModel.deleteClientUiState.collectAsState()
    val exportPdfState by viewModel.exportPdfUiState.collectAsState()
    val exportExcelState by viewModel.exportExcelUiState.collectAsState()
    val userStore by viewModel.userStoreState.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var showProBottomSheet by rememberSaveable { mutableStateOf(false) }
    var showLoadingExportDialog by rememberSaveable { mutableStateOf(true) }
    var clientIdName by remember { mutableStateOf(Pair(0, "")) }
    val isExporting = exportPdfState is FormUIState.Loading || exportExcelState is FormUIState.Loading
    val exportPdfTitle = stringResource(R.string.export_to_pdf)
    val exportExcelTitle = stringResource(R.string.exporter_en_excel)

    var expanded by remember { mutableStateOf(false) }
    var pendingExport by remember { mutableStateOf(ExportType.Pdf) }
    var proBottomSheetTitle by rememberSaveable { mutableStateOf("") }

    val exportPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            when (pendingExport) {
                ExportType.Pdf -> viewModel.exportClientsToPdf(context)
                ExportType.Excel -> viewModel.exportClientsToExcel(context)
            }
        } else {
            Toast.makeText(context, context.getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    val launchExport: (ExportType) -> Unit = { exportType ->
        focusManager.clearFocus()
        expanded = false
        if (!userStore.isProActive) {
            proBottomSheetTitle = when (exportType) {
                ExportType.Pdf -> exportPdfTitle
                ExportType.Excel -> exportExcelTitle
            }
            showProBottomSheet = true
        } else {
            showLoadingExportDialog = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                when (exportType) {
                    ExportType.Pdf -> viewModel.exportClientsToPdf(context)
                    ExportType.Excel -> viewModel.exportClientsToExcel(context)
                }
            } else {
                pendingExport = exportType
                exportPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
    }

    LaunchedEffect(refreshClientList) {
        clients.refresh()
    }

    LaunchedEffect(Unit) {
        if (fromDetail) {
            snackbarHostState.showSnackbar(
                SnackbarVisualsWithState(
                    message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                )
            )
        }
    }

    when (deleteClientState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                focusManager.clearFocus()
                snackbarHostState.currentSnackbarData?.dismiss()
                clients.refresh()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                    )
                )
                viewModel.resetFlow()
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = (deleteClientState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }

        else -> {}
    }

    when (exportPdfState) {
        is FormUIState.Loading -> {
            if (showLoadingExportDialog) {
                AppCustomDialog(modifier = Modifier.wrapContentSize(), setShowDialog = {
                    showLoadingExportDialog = it
                }) {
                    AppLoadingScreen(modifier = Modifier.wrapContentSize())
                }
            }
        }

        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                val file = (exportPdfState as FormUIState.Success).data
                context.showDownloadNotification(
                    file = file,
                    channelId = "client_export_pdf_channel",
                    channelName = context.getString(R.string.export_pdf_completed),
                    notificationId = 18
                )
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(R.string.export_pdf_saved)
                    )
                )
                viewModel.resetExportState()
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = (exportPdfState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetExportState()
            }
        }

        else -> {}
    }

    when (exportExcelState) {
        is FormUIState.Loading -> {
            if (showLoadingExportDialog) {
                AppCustomDialog(modifier = Modifier.wrapContentSize(), setShowDialog = {
                    showLoadingExportDialog = it
                }) {
                    AppLoadingScreen(modifier = Modifier.wrapContentSize())
                }
            }
        }

        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                val file = (exportExcelState as FormUIState.Success).data
                context.showDownloadNotification(
                    file = file,
                    channelId = "client_export_excel_channel",
                    channelName = context.getString(R.string.export_excel_completed),
                    notificationId = 19
                )
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(R.string.export_excel_saved)
                    )
                )
                viewModel.resetExportExcelState()
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = (exportExcelState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetExportExcelState()
            }
        }

        else -> {}
    }

    if (showProBottomSheet) {
        ProFeatureBottomSheet(
            title = proBottomSheetTitle,
            onDismiss = { showProBottomSheet = false },
            onUpgradeClick = {
                showProBottomSheet = false
                onNavigateToSubscription()
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AppHeadLine(
            text = stringResource(R.string.head_title_client, totalClientsCount),
            leadingContent = {
                IconButton(onClick = { expanded = true }) {
                    Icon(imageVector = AppIcons.Export, contentDescription = "Export client")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(White)
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.export_to_pdf)) },
                        enabled = !isExporting,
                        onClick = { launchExport(ExportType.Pdf) }
                    )

                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.exporter_en_excel)) },
                        enabled = !isExporting,
                        onClick = { launchExport(ExportType.Excel) }
                    )
                }
            }
        )

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
                    text = stringResource(com.groupec.salesb.core.ui.R.string.retry)
                ) {
                    clients.refresh()
                }
            }
        } else {
            if (!isSearching && clients.itemCount == 0) {
                AppLoadingScreen(text = stringResource(R.string.loading_clients))
            } else {
                AppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
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
                    placeholder = stringResource(com.groupec.salesb.core.ui.R.string.search_place_holder),
                    fieldType = FieldType.Text,
                    fieldColor = Silver,
                    shape = RoundedCornerShape(28.dp)
                )

                if (clients.itemCount == 0) {
                    EmptyScreen()
                } else {
                    ClientCardList(
                        clients = clients,
                        isSearching = isSearching,
                        onViewDetail = onViewDetail,
                        onDelete = { id, name ->
                            showDialog = true
                            clientIdName = Pair(id, name)
                        },
                        removeSelectedBgColor = removeSelectedBgColor
                    )
                }

                if (showDialog) {
                    AppAlertInfoDialog(
                        setShowDialog = {
                            showDialog = it
                            focusManager.clearFocus()
                        },
                        title = stringResource(
                            com.groupec.salesb.core.ui.R.string.confirm_delete_message,
                            clientIdName.second
                        ),
                        onConfirmButton = {
                            viewModel.deleteClient(clientIdName.first)
                        },
                        onDismissButton = {
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }
}
