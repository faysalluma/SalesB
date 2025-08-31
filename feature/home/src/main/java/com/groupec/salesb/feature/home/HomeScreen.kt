package com.groupec.salesb.feature.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.component.UnderlinedTextButton
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.Yellow
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.ProductsWithLowInventoryList
import com.groupec.salesb.core.ui.StatisticCard
import com.groupec.salesb.core.ui.StatisticChart
import kotlin.collections.get

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateToSaleList: () -> Unit,
    navigateToProduct: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val userStoreState by viewModel.userStore.collectAsState()
    val chartValuesState by viewModel.chartValues.collectAsState()
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
            .padding(horizontal = 16.dp)
    ) {
        // Check if show home content
        if (
           /* !privileges.containsAll(Privileges.Home.getKeysByApprovals(
                listOf(
                    Approval.STAT_PERIODIC,
                    Approval.STAT_NON_PERIODIC,
                    Approval.STAT_CHART,
                )
            ))*/
            false
        ) {
            EmptyScreen(text = stringResource(R.string.no_visual_allowed))
        } else {
            val heigthModifier = Modifier.height(34.dp)
            HeadLigne(context, viewModel, navigateToSaleList)
            StatisticPeriodic(context, viewModel, navigateToSaleList)
            Spacer(modifier = heigthModifier)
            StatisticNonPeriodic(viewModel, navigateToProduct)
            Spacer(modifier = heigthModifier)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (chartValuesState.isNotEmpty()) {
                    StatisticChart(modifier = Modifier.fillMaxWidth(0.75f), chartValuesState)
                } else {
                    Text(stringResource(R.string.no_data))
                }
            }
        }
    }
}

@Composable
fun HeadLigne(context: Context, viewModel: HomeViewModel, navigateToSaleList: () -> Unit) {
    val totalAmountOutputState by viewModel.totalAmountOutputs.collectAsState()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsState()
    val profits by remember { derivedStateOf { totalAmountSalesState - totalAmountOutputState }}
    val periodList = Period.entries.map { it.getTitle(context) }
    var periodValue by remember { mutableStateOf(periodList[1]) }
    val parameterState by viewModel.parameter.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        // Left element
        Row(
            Modifier.padding(top = 8.dp)
        ) {
            TitleLarge(
                modifier = Modifier.padding(end = 8.dp),
                title = stringResource(R.string.profit_label, profits.formatAmount(), parameterState.devise),
                color = if (profits >=0) Green else Red
            )
            TitleLarge(
                title = stringResource(R.string.output_label, totalAmountOutputState.formatAmount(), parameterState.devise)
            )
        }
        
        // Right element
        Row {
            UnderlinedTextButton(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.see_more),
                onClick = navigateToSaleList
            ) 
            
            Box(modifier = Modifier.width(200.dp)) {
                AppExposedDropdownMenu(
                    items = periodList,
                    value = periodValue,
                    onValueChange = { newPeriod ->
                        periodValue = newPeriod
                    }
                ) { index, item ->
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
        }
    }
}

@Composable
fun StatisticPeriodic(
    context: Context,
    viewModel: HomeViewModel,
    navigateToSaleList: () -> Unit
) {
    val parameterState by viewModel.parameter.collectAsState()
    val totalSalesState by viewModel.totalSales.collectAsState()
    val totalAmountSalesState by viewModel.totalAmountSales.collectAsState()
    val topSaleProductsState by viewModel.topSaleProducts.collectAsState()

    Column {
        TitleMedium(
            title = stringResource(R.string.title_stat_period),
        )
        Row (
            modifier = Modifier.padding(top = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val cardModifier = Modifier.weight(1f)
            SaleStatisticCard(
                modifier = cardModifier,
                numberTitle = totalSalesState,
                dataValue = totalAmountSalesState.formatAmount(),
                devise = parameterState.devise,
                navigateToSaleList = navigateToSaleList
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
    navigateToProduct: () -> Unit
) {
    val totalProductsState by viewModel.totalProducts.collectAsState()
    val totalAlertSeuilState by viewModel.totalAlertSeuilProducts.collectAsState()
    val productsWithLowInventoryState by viewModel.productsWithLowInventoryUiState.collectAsState()

    TitleMedium(
        title = stringResource(R.string.title_stat_no_period),
    )
    Row (
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ){
        val cardModifier = Modifier.weight(1f)
        ProductStatisticCard(
            modifier = cardModifier,
            dataValue = totalProductsState.toString(),
            navigateToProduct = navigateToProduct
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