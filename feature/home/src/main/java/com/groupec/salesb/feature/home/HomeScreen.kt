package com.groupec.salesb.feature.home

import android.content.Context
import android.widget.Toast
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
    val userStoreState by viewModel.userStore.collectAsStateWithLifecycle()
    val chartValuesState by viewModel.chartValues.collectAsStateWithLifecycle()
    val privileges = userStoreState.getPrivileges()

    ComposableLifecycle(
        onCreate = {
            val startDate = Period.Today.startDate
            val endDate = Period.Today.endDate
            viewModel.apply {
                getTotalSale(startDate, endDate)
                getTopSaleProducts(startDate, endDate)
                getTotalProduct()
                getTotalAlertSeuil()
                getChartDataToday(context, startDate)
            }
        }
    )

    Column (
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
    ) {

        // Check if show home content
        if (
            privileges.any{
                it in Privileges.Home.getKeysByApprovals(
                    listOf(
                        Approval.STAT_PERIODIC,
                        Approval.STAT_NON_PERIODIC,
                        Approval.STAT_CHART,
                    )
                )
            }
        ) {
            val heigthModifier = Modifier.height(34.dp)

            // Headline
            if (
                privileges.any{
                    it in Privileges.Home.getKeysByApprovals(
                        listOf(
                            Approval.STAT_PERIODIC,
                            Approval.STAT_CHART,
                        )
                    )
                }
            ) {
                HeadLigne(context, viewModel,privileges, navigateToSaleList)
            }

            // Periodic statistic
            if (privileges.contains(Privileges.Home.getKeyByApproval(
                    Approval.STAT_PERIODIC
                ))) {
                StatisticPeriodic(context, viewModel,privileges, navigateToSaleList)
                Spacer(modifier = heigthModifier)
            }

            // Non-Periodic statistic
            if (privileges.contains(Privileges.Home.getKeyByApproval(
                    Approval.STAT_NON_PERIODIC
                ))) {
                StatisticNonPeriodic(viewModel,privileges, navigateToProduct)
                Spacer(modifier = heigthModifier)
            }

            // Chart statistic
            if (privileges.contains(Privileges.Home.getKeyByApproval(
                    Approval.STAT_CHART
                ))) {
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
    }
}

@Composable
fun HeadLigne(
    context: Context,
    viewModel: HomeViewModel,
    privileges: List<String>,
    navigateToSaleList: () -> Unit
) {
    val totalAmountOutputState by viewModel.totalAmountOutputs.collectAsStateWithLifecycle()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsStateWithLifecycle()
    val profits by remember { derivedStateOf { totalAmountSalesState - totalAmountOutputState } }
    val parameterState by viewModel.parameter.collectAsStateWithLifecycle()

    val periodList = Period.entries.map { it.getTitle(context) }
    var periodValue by rememberSaveable { mutableStateOf(periodList[1]) }
    val profitColor = if (profits >= 0) Green else Red

    ExpandedLayout(
        context = context,
        profits = profits,
        totalAmountOutputState = totalAmountOutputState,
        parameterState = parameterState,
        profitColor = profitColor,
        periodList = periodList,
        periodValue = periodValue,
        onPeriodChange = { periodValue = it },
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
                    val startDate = Period.entries[index].startDate
                    val endDate = Period.entries[index].endDate
                    viewModel.apply {
                        getTotalSale(startDate, endDate)
                        getTopSaleProducts(startDate, endDate)
                        when (Period.entries[index]) {
                            Period.Yesterday, Period.Today -> getChartDataToday(context, startDate)
                            Period.Week, Period.Month -> getChartDataByDate(startDate, endDate)
                        }
                    }
                }
            }

            if (
                privileges.any{
                    it in Privileges.MySales.getKeysByApprovals(
                        listOf(
                            Approval.AUTHORIZE_ADD,
                            Approval.AUTHORIZE_EDIT,
                            Approval.AUTHORIZE_DELETE,
                        )
                    )
                }
            )
            {
                IconTextButton(
                    contentPadding = PaddingValues(19.dp),
                    text = stringResource(R.string.see_more),
                    colors = ButtonDefaults.buttonColors(containerColor = Silver, contentColor = Primary),
                    onClick = navigateToSaleList
                )
            }
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
    val context = LocalContext.current
    val parameterState by viewModel.parameter.collectAsStateWithLifecycle()
    val totalSalesState by viewModel.totalSales.collectAsStateWithLifecycle()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsStateWithLifecycle()
    val topSaleProductsState by viewModel.topSaleProducts.collectAsStateWithLifecycle()

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
                    if (
                        privileges.any{
                            it in Privileges.MySales.getKeysByApprovals(
                                listOf(
                                    Approval.AUTHORIZE_ADD,
                                    Approval.AUTHORIZE_EDIT,
                                    Approval.AUTHORIZE_DELETE,
                                )
                            )
                        }
                    ) {
                        navigateToSaleList()
                    } else {
                        Toast.makeText(context, context.getString(com.groupec.salesb.core.R.string.no_visual_allowed),
                            Toast.LENGTH_SHORT).show()
                    }
                }
            )
            TopSaleStatisticCard(
                modifier = cardModifier,
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
    privileges: List<String>,
    navigateToProduct: () -> Unit
) {
    val context = LocalContext.current
    val totalProductsState by viewModel.totalProducts.collectAsStateWithLifecycle()
    val totalAlertSeuilState by viewModel.totalAlertSeuilProducts.collectAsStateWithLifecycle()
    val productsWithLowInventoryState by viewModel.productsWithLowInventoryUiState.collectAsStateWithLifecycle()

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
            dataValue = totalProductsState.toString(),
            navigateToProduct = {
                if (
                    privileges.any{
                        it in Privileges.Product.getKeysByApprovals(
                            listOf(
                                Approval.AUTHORIZE_ADD,
                                Approval.AUTHORIZE_EDIT,
                                Approval.AUTHORIZE_DELETE,
                            )
                        )
                    }
                ) {
                    navigateToProduct()
                } else {
                    Toast.makeText(context, context.getString(com.groupec.salesb.core.R.string.no_visual_allowed),
                        Toast.LENGTH_SHORT).show()
                }
            }
        )
        AlertInventoryStatisticCard(
            modifier = cardModifier,
            dataValue = totalAlertSeuilState.toString(),
            productsWithLowInventoryState = productsWithLowInventoryState,
            getProductsWithLowInventory = viewModel::getProductsWithLowInventory
        )
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
fun TopSaleStatisticCard(modifier: Modifier = Modifier, numberTitle: Int ? = null, dataValue: String ?, devise: String ? = null) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_top,
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
    dataValue: String ?,
    devise: String ? = null,
    navigateToProduct: () -> Unit
) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_product,
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
                            Toast.makeText(context, context.getString(R.string.products_copied), Toast.LENGTH_SHORT).show()
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
                            products = products
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}