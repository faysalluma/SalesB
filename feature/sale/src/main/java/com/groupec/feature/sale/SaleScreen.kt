package com.groupec.feature.sale

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.IconTextButton
import com.groupec.salesb.core.designsystem.component.Position
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.FeatureAccess
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.model.data.others.paymentTypeFromLabel
import com.groupec.salesb.core.model.data.others.paymentTypeFromValue
import com.groupec.salesb.core.model.data.others.paymentTypeLabels
import com.groupec.salesb.core.model.data.others.paymentTypeValue
import com.groupec.salesb.core.print.Print
import com.groupec.salesb.core.print.PrintAction
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.ProductGrid
import com.groupec.salesb.core.ui.ProFeatureBottomSheet
import com.groupec.salesb.core.ui.SaleDetailCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SaleScreen(
    snackbarHostState: SnackbarHostState,
    isExpandedWidth: Boolean,
    modifier: Modifier = Modifier,
    viewModel: SaleViewModel = hiltViewModel(),
    navigateToProduct: () -> Unit,
    navigateToClient: () -> Unit,
    onNavigateToSubscription: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val products = viewModel.pagedProducts.collectAsLazyPagingItems()
    val error = (products.loadState.refresh as? LoadState.Error)?.error?.message
    val parameter by viewModel.parameter.collectAsState()
    val clientsPairState by viewModel.clientsUiPairState.collectAsState()
    val userStore by viewModel.userStoreState.collectAsState()
    val addSaleUiState by viewModel.addSaleUiState.collectAsState()
    val isLoading = addSaleUiState is FormUIState.Loading
    var showSummary by remember { mutableStateOf(false) }
    var showProBottomSheet by remember { mutableStateOf(false) }
    var proBottomSheetTitle by rememberSaveable { mutableStateOf("") }
    val salesLimitTitle = stringResource(com.groupec.salesb.core.ui.R.string.pro_feature_sales_limit_title)
    val receiptPrintTitle = stringResource(com.groupec.salesb.core.ui.R.string.pro_feature_receipt_print_title)

    // For selected Products and handling of multiples textfield created
    val selectedProducts = remember { mutableStateListOf<Pair<Int, Product>>() }
    val textFieldValues = remember { mutableStateMapOf<Int, String>() }
    val quantityCheck = remember { mutableStateMapOf<Int, Boolean>() }

    // Bluetooth
    val bluetoothPrint = remember(context) { Print(context) }

    val onQuantityChange: (Pair<Int, Product>) -> Unit = { productLine ->
        val (index, product) = productLine
        val isIntegerQuantityMode = parameter.serviceview || parameter.useintforpriceandamout
        val quantityValue = textFieldValues[index]?.takeIf { it.isNotEmpty() }?.toDoubleOrNull() ?: 1.0
        if (quantityValue <= 0) {
            selectedProducts.removeAll { it.first == index }
            textFieldValues.remove(index)
            quantityCheck.remove(index)
        } else {
            val normalizedQuantity = if (isIntegerQuantityMode) {
                quantityValue.toInt().toString()
            } else {
                quantityValue.toString()
            }
            textFieldValues[index] = normalizedQuantity
            // Check if quantityValue > product quantity
            val quantityForStock = if (isIntegerQuantityMode) quantityValue.toInt().toDouble() else quantityValue
            quantityCheck[index] = product.qtestock?.let { quantityForStock > it } ?: false
        }
    }

    val totalAmount by remember {
        derivedStateOf {
            selectedProducts.fold(0.0) { acc, productLine ->
                val quantity = textFieldValues[productLine.first]?.toDoubleOrNull() ?: 0.0
                acc + (productLine.second.prixttc * quantity)
            }
        }
    }
    val itemsCount by remember {
        derivedStateOf {
            selectedProducts.fold(0.0) { acc, productLine ->
                val quantity = textFieldValues[productLine.first]?.toDoubleOrNull() ?: 0.0
                acc + quantity
            }
        }
    }

    // For payment type selector
    val paymentTypeList = paymentTypeLabels(context)
    val firstPaymentTypeDefaultValue = when {
        !parameter.activepaymentmode -> ""
        else -> {
            paymentTypeFromValue(parameter.defaultpaymenttype)?.let { type ->
                context.getString(type.libelleRes)
            } ?: parameter.defaultpaymenttype
                .takeIf { it in paymentTypeList }
                ?: ""
        }
    }
    var paymentTypeState by remember { mutableStateOf(firstPaymentTypeDefaultValue) }
    val paymentTypeValueForSave = paymentTypeFromLabel(context, paymentTypeState)?.let(::paymentTypeValue)
    var clientlibelleState by remember {
        mutableStateOf(TextFieldValue(""))
    }
    var selectedClientId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(parameter.activepaymentmode, firstPaymentTypeDefaultValue) {
        if (!parameter.activepaymentmode) {
            paymentTypeState = ""
        } else if (paymentTypeState.isBlank()) {
            paymentTypeState = firstPaymentTypeDefaultValue
        }
    }

    LaunchedEffect(parameter.activeClient) {
        if (!parameter.activeClient) {
            clientlibelleState = TextFieldValue("")
            selectedClientId = null
        }
    }

    LaunchedEffect(selectedProducts.size) {
        if (selectedProducts.isEmpty()) {
            showSummary = false
            paymentTypeState = firstPaymentTypeDefaultValue
            clientlibelleState = TextFieldValue("")
            selectedClientId = null
        }
    }

    LaunchedEffect(parameter.serviceview, parameter.useintforpriceandamout) {
        if (parameter.serviceview || parameter.useintforpriceandamout) {
            selectedProducts.forEach { productLine ->
                val productId = productLine.first
                val value = textFieldValues[productId]?.toDoubleOrNull() ?: return@forEach
                textFieldValues[productId] = value.toInt().toString()
                quantityCheck[productId] = productLine.second.qtestock?.let { value.toInt() > it } ?: false
            }
        }
    }

    // Update paramater when back to handle service
    ComposableLifecycle(
        onResume = {
            viewModel.getParameter()
            viewModel.getClients()
        }
    )
    when (addSaleUiState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                val (printAction, sale) = (addSaleUiState as FormUIState.Success).data
                // Consume state immediately to avoid re-triggering on configuration change.
                viewModel.resetFlow()
                when (printAction) {
                    PrintAction.Thermal -> {
                        scope.launch {
                            val result = withContext(Dispatchers.IO) {
                                bluetoothPrint.printWithResult(
                                    parameter.logo,
                                    sale = sale,
                                    parameter = parameter
                                )
                            }
                            result.exceptionOrNull()?.message?.let { message ->
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else -> {}
                }
                // Close portrait summary immediately to avoid transient EmptyScreen flicker after save.
                showSummary = false
                selectedProducts.clear()
                textFieldValues.clear()
                paymentTypeState = firstPaymentTypeDefaultValue
                clientlibelleState = TextFieldValue("")
                selectedClientId = null
                products.refresh()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithState(
                            message = context.getString(
                                com.groupec.salesb.core.ui.R.string.product_operate_succesfully
                            )
                        )
                    )
                }
            }
        }
        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                // Consume state immediately to avoid re-triggering on configuration change.
                viewModel.resetFlow()
                scope.launch {
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithState(
                            message = (addSaleUiState as FormUIState.Error).message,
                            isError = true
                        )
                    )
                }
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

    // When not in expanded mode
    if (!isExpandedWidth) {
        if (showSummary) {
            // When pop back we are on resume page
            BackHandler {
                showSummary = false
            }

            // Resume screen
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                SaleDetailScreen(
                    modifier = Modifier.fillMaxSize(),
                    selectedProducts = selectedProducts,
                    textFieldValues = textFieldValues,
                    quantityCheck = quantityCheck,
                    parameter = parameter,
                    isLoading = isLoading,
                    onSave = { total, printAction ->
                        val saleDetail = selectedProducts.map { productLine ->
                            val quantity = textFieldValues[productLine.first]
                            SaleDetail(
                                id = productLine.second.id!!,
                                qte = quantity?.toDouble() ?: 0.0,
                                libelle = productLine.second.libelle,
                                prix = productLine.second.prixttc
                            )
                        }
                        val sale = Sale(
                            totalprix = total,
                            paymenttype = paymentTypeValueForSave,
                            clientid = selectedClientId.takeIf { parameter.activeClient },
                            details = saleDetail
                        )
                        if (printAction != PrintAction.Thermal &&
                            !FeatureAccess.canCreateSale(
                                isProActive = userStore.isProActive,
                                hasReachedFreeMonthlySalesLimit = viewModel.hasReachedFreeMonthlySalesLimit(),
                            )
                        ) {
                            proBottomSheetTitle = salesLimitTitle
                            showProBottomSheet = true
                            return@SaleDetailScreen false
                        }
                        if (printAction == PrintAction.Thermal) {
                            if (!FeatureAccess.canPrintReceipt(userStore.isProActive)) {
                                proBottomSheetTitle = receiptPrintTitle
                                showProBottomSheet = true
                                return@SaleDetailScreen false
                            }
                            val result = withContext(Dispatchers.IO) {
                                bluetoothPrint.validatePrinterReadiness()
                            }
                            val errorMessage = result.exceptionOrNull()?.message
                            if (errorMessage != null) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                false
                            } else {
                                viewModel.addSale(sale, printAction)
                                true
                            }
                        } else {
                            viewModel.addSale(sale, printAction)
                            true
                        }
                    },
                    onClear = {
                        selectedProducts.clear()
                        textFieldValues.clear()
                        paymentTypeState = firstPaymentTypeDefaultValue
                        clientlibelleState = TextFieldValue("")
                        selectedClientId = null
                    },
                    onQuantityChange = onQuantityChange,
                    paymentTypeState = paymentTypeState,
                    onPaymenTypeSelected = {
                        paymentTypeState = it
                    },
                    clientItems = clientsPairState,
                    clientlibelleState = clientlibelleState,
                    onClientlibelleState = {
                        clientlibelleState = it
                        if (clientsPairState.none { client -> client.second == it.text }) {
                            selectedClientId = null
                        }
                    },
                    onClientSelected = { client ->
                        selectedClientId = client.first.toIntOrNull()
                    },
                    navigateToClient = navigateToClient
                )
            }
        } else {
            // Select product screen
            Box(modifier = Modifier.fillMaxSize()) {
                ProductSelectionSection(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    error = error,
                    isSearching = isSearching,
                    products = products,
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::updateSearchQuery,
                    onClearSearch = { viewModel.updateSearchQuery("") },
                    navigateToProduct = navigateToProduct,
                    selectedProducts = selectedProducts,
                    textFieldValues = textFieldValues,
                    isExpandedWidth = isExpandedWidth,
                    parameter = parameter,
                    onQuantityChange = onQuantityChange
                )
                // Bottom floating card
                if (selectedProducts.isNotEmpty()) {

                    val totalLabel = totalAmount.formatAmount().plus(" ${parameter.devise}")
                    val isIntegerQuantityMode = parameter.serviceview || parameter.useintforpriceandamout
                    val itemLabel = if (isIntegerQuantityMode) {
                        selectedProducts.sumOf { productLine ->
                            textFieldValues[productLine.first]?.toDoubleOrNull()?.toInt() ?: 0
                        }.toString()
                    } else {
                        itemsCount.autoRound()
                    }

                    val onShowSummary = { showSummary = true}
                    val stockLimit = quantityCheck.values.any { it }
                    if (!stockLimit) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                        ) {
                            /*if (isTablet()) {
                                LargeFloatingActionButton(
                                    onClick = onShowSummary,
                                    shape = CircleShape,
                                    containerColor = Primary,
                                    contentColor = White,
                                ) {
                                    Text(
                                        text = itemLabel,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                }
                            } else {
                                FloatingActionButton(
                                    onClick = onShowSummary,
                                    shape = CircleShape,
                                    containerColor = Primary,
                                    contentColor = White,
                                ) {
                                    Text(
                                        text = itemLabel,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }*/

                            Card(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Silver),
                                elevation = CardDefaults.cardElevation(6.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = stringResource(R.string.cart_items_label, itemLabel),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = totalLabel,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                    IconTextButton (
                                        modifier = Modifier.wrapContentWidth().padding(start = 8.dp),
                                        icon = {
                                            Icon(
                                                imageVector = AppIcons.Next,
                                                contentDescription = stringResource(R.string.view_resume)
                                            )
                                        },
                                        position = Position.Right,
                                        text = stringResource(R.string.view_resume)
                                    ) {
                                        showSummary = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    } else {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(modifier = Modifier.weight(0.6f)) {
                ProductSelectionSection(
                    modifier = Modifier.fillMaxSize(),
                    error = error,
                    isSearching = isSearching,
                    products = products,
                    searchQuery = searchQuery,
                    onSearchChange = viewModel::updateSearchQuery,
                    onClearSearch = { viewModel.updateSearchQuery("") },
                    navigateToProduct = navigateToProduct,
                    selectedProducts = selectedProducts,
                    textFieldValues = textFieldValues,
                    isExpandedWidth = isExpandedWidth,
                    onQuantityChange = onQuantityChange,
                    parameter = parameter
                )
            }

            Box(modifier = Modifier.weight(0.4f)) {
                SaleDetailScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 18.dp),
                    selectedProducts = selectedProducts,
                    textFieldValues = textFieldValues,
                    quantityCheck = quantityCheck,
                    parameter = parameter,
                    isLoading = isLoading,
                    onSave = { total, printAction ->
                        val saleDetail = selectedProducts.map { productLine ->
                            val quantity = textFieldValues[productLine.first]
                            SaleDetail(
                                id = productLine.second.id!!,
                                qte = quantity?.toDouble() ?: 0.0,
                                libelle = productLine.second.libelle,
                                prix = productLine.second.prixttc
                            )
                        }
                        val sale = Sale(
                            totalprix = total,
                            paymenttype = paymentTypeValueForSave,
                            clientid = selectedClientId.takeIf { parameter.activeClient },
                            details = saleDetail
                        )
                        if (printAction != PrintAction.Thermal &&
                            !FeatureAccess.canCreateSale(
                                isProActive = userStore.isProActive,
                                hasReachedFreeMonthlySalesLimit = viewModel.hasReachedFreeMonthlySalesLimit(),
                            )
                        ) {
                            proBottomSheetTitle = salesLimitTitle
                            showProBottomSheet = true
                            return@SaleDetailScreen false
                        }
                        if (printAction == PrintAction.Thermal) {
                            if (!FeatureAccess.canPrintReceipt(userStore.isProActive)) {
                                proBottomSheetTitle = receiptPrintTitle
                                showProBottomSheet = true
                                return@SaleDetailScreen false
                            }
                            val result = withContext(Dispatchers.IO) {
                                bluetoothPrint.validatePrinterReadiness()
                            }
                            val errorMessage = result.exceptionOrNull()?.message
                            if (errorMessage != null) {
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                false
                            } else {
                                viewModel.addSale(sale, printAction)
                                true
                            }
                        } else {
                            viewModel.addSale(sale, printAction)
                            true
                        }
                    },
                    onClear = {
                        selectedProducts.clear()
                        textFieldValues.clear()
                        paymentTypeState = firstPaymentTypeDefaultValue
                        clientlibelleState = TextFieldValue("")
                        selectedClientId = null
                    },
                    onQuantityChange = onQuantityChange,
                    paymentTypeState = paymentTypeState,
                    onPaymenTypeSelected = {
                        paymentTypeState = it
                    },
                    clientItems = clientsPairState,
                    clientlibelleState = clientlibelleState,
                    onClientlibelleState = {
                        clientlibelleState = it
                        if (clientsPairState.none { client -> client.second == it.text }) {
                            selectedClientId = null
                        }
                    },
                    onClientSelected = { client ->
                        selectedClientId = client.first.toIntOrNull()
                    },
                    navigateToClient = navigateToClient
                )
            }
        }
    }
}

@Composable
fun SaleDetailScreen(
    modifier: Modifier,
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: MutableMap<Int, Boolean>,
    parameter: Parameter,
    isLoading: Boolean,
    onSave: suspend (Double, PrintAction) -> Boolean,
    onClear: () -> Unit,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    paymentTypeState: String,
    onPaymenTypeSelected: (String) -> Unit,
    clientItems: List<Pair<String, String>>,
    clientlibelleState: TextFieldValue,
    onClientlibelleState: (TextFieldValue) -> Unit,
    onClientSelected: (Pair<String, String>) -> Unit,
    navigateToClient: () -> Unit
) {
    SaleDetailCard(
        modifier = modifier,
        selectedProducts = selectedProducts,
        textFieldValues = textFieldValues,
        parameter = parameter,
        isLoading = isLoading,
        onQuantityChange = onQuantityChange,
        onSave = onSave,
        onClear = onClear,
        quantityCheck = quantityCheck,
        paymentTypeState = paymentTypeState,
        onPaymenTypeSelected = onPaymenTypeSelected,
        clientItems = clientItems,
        clientlibelleState = clientlibelleState,
        onClientlibelleState = onClientlibelleState,
        onClientSelected = onClientSelected,
        navigateToClient = navigateToClient
    )
}

@Composable
private fun ProductSelectionSection(
    error: String?,
    isSearching: Boolean,
    products: LazyPagingItems<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    navigateToProduct: () -> Unit,
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    isExpandedWidth:  Boolean,
    parameter: Parameter,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = parameter.serviceview,
        plural = true
    )
    val catalogLabelSingular = context.getCatalogItemLabel(
        isServiceView = parameter.serviceview,
        plural = false
    )

    // if get error when fetching products
    if (error != null) {
        Column(
            modifier = modifier
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
                products.refresh()
            }
        }
    } else {
        // Show Progress bar waiting load products
        if (!isSearching && products.itemCount == 0) {
            AppLoadingScreen(text = stringResource(R.string.loading_products, catalogLabelPlural))
        } else {
            Column(modifier = modifier) {
                // Head
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TitleLarge(
                        title = stringResource(R.string.my_products, catalogLabelPlural),
                        modifier = Modifier.padding(top = 12.dp, end = 8.dp)
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
                                IconButton(onClick = onClearSearch) {
                                    Icon(
                                        imageVector = AppIcons.Close,
                                        contentDescription = "Clear text"
                                    )
                                }
                            }
                        },
                        onChange = onSearchChange,
                        placeholder = stringResource(R.string.search_product_place_holder),
                        fieldType = FieldType.Text,
                        fieldColor = Silver,
                        shape = RoundedCornerShape(28.dp)
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(top = 12.dp))

                // Paginated list
                if (products.itemCount == 0) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        DefaultButton(
                            modifier = Modifier.wrapContentWidth(),
                            text = stringResource(R.string.add_product, catalogLabelSingular),
                            onClick = navigateToProduct
                        )
                    }
                } else {
                    ProductGrid(
                        products = products,
                        selectedProducts = selectedProducts,
                        textFieldValues = textFieldValues,
                        isSearching = isSearching,
                        isExpandedWidth = isExpandedWidth,
                        onQuantityChange = onQuantityChange,
                        parameter = parameter
                    )
                }
            }
        }
    }
}
