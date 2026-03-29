package com.groupec.salesb.feature.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.IconTextButton
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Yellow
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.ProductsWithLowInventoryList
import com.groupec.salesb.core.ui.StatisticCard
import com.groupec.salesb.core.ui.StatisticChart

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToSaleList: () -> Unit,
    navigateToProduct: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { }
    val userStoreState by viewModel.userStore.collectAsStateWithLifecycle()
    val chartValuesState by viewModel.chartValues.collectAsStateWithLifecycle()
    val selectedPeriod by viewModel.selectedPeriod.collectAsStateWithLifecycle()
    val privileges = userStoreState.getPrivileges()
    val parameterState by viewModel.parameter.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    ComposableLifecycle(
        onResume = {
            viewModel.refreshDashboard(context)
        }
    )

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        val heigthModifier = Modifier.height(34.dp)

        // Headline
        HeadLigne(
            context = context,
            viewModel = viewModel,
            parameter = parameterState,
            privileges = privileges,
            navigateToSaleList = navigateToSaleList,
            selectedPeriod = selectedPeriod
        )

        // Periodic statistic
        StatisticPeriodic(context, viewModel,privileges, navigateToSaleList)
        Spacer(modifier = heigthModifier)

        // Non-Periodic statistic
        StatisticNonPeriodic(viewModel, parameterState, privileges, navigateToProduct)
        Spacer(modifier = heigthModifier)

        // Chart statistic
        Box(
            modifier = Modifier
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (chartValuesState.isNotEmpty()) {
                StatisticChart(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(16.dp),
                    values = chartValuesState
                )
            } else {
                Text(stringResource(R.string.no_data))
            }
        }
    }
}

@Composable
fun HeadLigne(
    context: Context,
    viewModel: HomeViewModel,
    parameter: Parameter,
    privileges: List<String>,
    navigateToSaleList: () -> Unit,
    selectedPeriod: Period
) {
    val totalAmountOutputState by viewModel.totalAmountOutputs.collectAsStateWithLifecycle()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsStateWithLifecycle()
    val profits by remember { derivedStateOf { totalAmountSalesState - totalAmountOutputState } }

    val periodList = Period.entries.map { it.getTitle(context) }
    val periodValue = selectedPeriod.getTitle(context)
    val profitColor = if (profits >= 0) Green else Red

    ExpandedLayout(
        context = context,
        profits = profits,
        totalAmountOutputState = totalAmountOutputState,
        parameterState = parameter,
        profitColor = profitColor,
        periodList = periodList,
        periodValue = periodValue,
        onPeriodChange = { newPeriod ->
            Period.entries.firstOrNull { it.getTitle(context) == newPeriod }?.let { period ->
                viewModel.onPeriodChange(period, context)
            }
        },
        viewModel = viewModel,
        privileges = privileges,
        navigateToSaleList = navigateToSaleList
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ExpandedLayout(
    context: Context,
    profits: Double,
    totalAmountOutputState: Double,
    parameterState: Parameter,
    profitColor: Color,
    periodList: List<String>,
    periodValue: String,
    onPeriodChange: (String) -> Unit,
    viewModel: HomeViewModel,
    privileges: List<String>,
    navigateToSaleList: () -> Unit
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Left
        FlowRow(
            modifier = Modifier
                .weight(1f)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.Start,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TitleLarge(
                modifier = Modifier.padding(end = 8.dp),
                title = stringResource(
                    R.string.profit_label,
                    profits.formatAmount(),
                    parameterState.devise
                ),
                color = profitColor
            )
            TitleLarge(
                title = stringResource(
                    R.string.output_label,
                    totalAmountOutputState.formatAmount(),
                    parameterState.devise
                )
            )
        }

        // Right
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End
        ) {

            Box(
                modifier = Modifier
                    .width(200.dp)
                    .padding(end = 14.dp)
            ) {
                AppExposedDropdownMenu(
                    items = periodList,
                    value = periodValue,
                    onValueChange = { newPeriod -> onPeriodChange(newPeriod) }
                ) { index, _ ->
                    viewModel.onPeriodChange(Period.entries[index], context)
                }
            }

            IconTextButton(
                contentPadding = PaddingValues(19.dp),
                text = stringResource(R.string.see_more),
                colors = ButtonDefaults.buttonColors(containerColor = Silver, contentColor = Primary),
                onClick = navigateToSaleList
            )
        }
    }
}

@Composable
fun StatisticPeriodic(
    context: Context,
    viewModel: HomeViewModel,
    privileges: List<String>,
    navigateToSaleList: () -> Unit
) {
    val parameterState by viewModel.parameter.collectAsStateWithLifecycle()
    val totalSalesState by viewModel.totalSales.collectAsStateWithLifecycle()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsStateWithLifecycle()
    val topSaleProductsState by viewModel.topSaleProducts.collectAsStateWithLifecycle()
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = parameterState.serviceview,
        plural = true
    )

    Column {
        TitleMedium(
            title = stringResource(R.string.title_stat_period),
        )
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max)
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val cardModifier = Modifier.weight(1f)
            SaleStatisticCard(
                modifier = cardModifier,
                numberTitle = totalSalesState,
                dataValue = totalAmountSalesState.formatAmount(),
                devise = parameterState.devise,
                navigateToSaleList = {
                    navigateToSaleList()
                }
            )
            TopSaleStatisticCard(
                modifier = cardModifier,
                labelText = stringResource(R.string.statistic_label_top, catalogLabelPlural),
                dataValue = topSaleProductsState
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString { "${it.libelle} (${it.qtestock})" } ?: context.getString(R.string.no_data),
            )
        }
    }
}


@Composable
fun StatisticNonPeriodic(
    viewModel: HomeViewModel,
    parameter: Parameter,
    privileges: List<String>,
    navigateToProduct: () -> Unit
) {
    val context = LocalContext.current
    val totalProductsState by viewModel.totalProducts.collectAsStateWithLifecycle()
    val totalAlertSeuilState by viewModel.totalAlertSeuilProducts.collectAsStateWithLifecycle()
    val productsWithLowInventoryState by viewModel.productsWithLowInventoryUiState.collectAsStateWithLifecycle()
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = parameter.serviceview,
        plural = true
    )

    TitleMedium(
        title = stringResource(R.string.title_stat_no_period),
    )
    Row (
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Max)
        ,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ){
        val cardModifier = Modifier.weight(1f)
        ProductStatisticCard(
            modifier = cardModifier,
            labelText = stringResource(R.string.statistic_label_product, catalogLabelPlural),
            dataValue = totalProductsState.toString(),
            navigateToProduct = {
                navigateToProduct()
            }
        )
        if (!parameter.serviceview) {
            AlertInventoryStatisticCard(
                modifier = cardModifier,
                labelText = stringResource(R.string.statistic_label_alert_inventory, catalogLabelPlural),
                copiedLabel = catalogLabelPlural,
                isServiceView = parameter.serviceview,
                dataValue = totalAlertSeuilState.toString(),
                productsWithLowInventoryState = productsWithLowInventoryState,
                getProductsWithLowInventory = viewModel::getProductsWithLowInventory
            )
        }
    }
}

/*
@Composable
fun RightDashBoard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 24.dp)) {
        TitleNormal(
            title = stringResource(R.string.title_last_sales),
            color = Green,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally).padding(bottom = 16.dp)
        )
        val sales = listOf(
            Sale(
                1,
                "12 Nov 2024 9:50",
                listOf(
                    Product(1, "Pain"),
                    Product(2, "Beurre")
                ),
                20.50
            ),
            Sale(
                2,
                "15 Sept 2024 08:04",
                listOf(
                    Product(1, "Sucre"),
                    Product(2, "Riz")
                ),
                980.00
            ),
            Sale(
                3,
                "30 Oct 2024 10:20",
                listOf(
                    Product(1, "Chocolat"),
                    Product(2, "Lait")
                ),
                220.50
            )
        )
        SaleCardList(sales = sales)
    }
}*/

@Composable
fun SaleStatisticCard(
    modifier: Modifier = Modifier,
    numberTitle: Int ? = null,
    dataValue: String ?,
    devise: String ? = null,
    navigateToSaleList: () -> Unit
) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_sale,
        iconColor = Green,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise,
        onclick = navigateToSaleList
    )
}

@Composable
fun TopSaleStatisticCard(
    modifier: Modifier = Modifier,
    numberTitle: Int ? = null,
    labelText: String? = null,
    dataValue: String ?,
    devise: String ? = null
) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_top,
        labelText = labelText,
        iconColor = Yellow,
        numberTitle = numberTitle,
        dataValue = dataValue,
        dataValueStyle = MaterialTheme.typography.titleSmall,
        devise = devise
    ) {

    }
}

@Composable
fun ProductStatisticCard(
    modifier: Modifier = Modifier,
    numberTitle: Int ? = null,
    labelText: String? = null,
    dataValue: String ?,
    devise: String ? = null,
    navigateToProduct: () -> Unit
) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_product,
        labelText = labelText,
        iconColor = Primary,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise,
        onclick = navigateToProduct
    )
}

@Composable
fun AlertInventoryStatisticCard(
    modifier: Modifier = Modifier,
    numberTitle: Int? = null,
    labelText: String? = null,
    copiedLabel: String,
    isServiceView: Boolean,
    dataValue: String?,
    devise: String? = null,
    productsWithLowInventoryState: UIState<List<Product>>,
    getProductsWithLowInventory: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val isLoadingProducts = productsWithLowInventoryState is UIState.Loading
    var products : List<Product>? = null
    val clipBoardManager = LocalClipboardManager.current

    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_alert_inventory,
        labelText = labelText,
        iconColor = Red,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise,
        onclick = {
            getProductsWithLowInventory()
            showDialog = true
        }
    )

    if (showDialog) {
        AppCustomDialog(setShowDialog = { showDialog = it } ) {
            Column {
                Box (
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.TopEnd
                ){
                    IconButton(
                        enabled = !isLoadingProducts,
                        onClick = {
                            val copiedProducts = products?.joinToString { "${it.libelle} (${it.qtestock})" } ?: ""
                            clipBoardManager.setText(
                                AnnotatedString(copiedProducts)
                            )
                            Toast.makeText(
                                context,
                                context.getString(R.string.products_copied, copiedLabel),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Icon(
                            imageVector = AppIcons.Copy,
                            contentDescription = "Copy products with low inventory label"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                when (productsWithLowInventoryState) {
                    is UIState.Loading -> AppLoadingScreen(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                    is UIState.Success -> {
                        products = productsWithLowInventoryState.data
                        ProductsWithLowInventoryList(
                            isServiceView = isServiceView,
                            products = products
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}
