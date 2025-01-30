package com.groupec.salesb.feature.home

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.component.UnderlinedTextButton
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.Yellow
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.StatisticCard
import com.groupec.salesb.core.ui.StatisticChart

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
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
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        // Check if show home content
        if (
            !privileges.containsAll(Privileges.Home.getKeysByApprovals(
                listOf(
                    Approval.STAT_PERIODIC,
                    Approval.STAT_NON_PERIODIC,
                    Approval.STAT_CHART,
                )
            ))
        ) {
            EmptyScreen(text = stringResource(R.string.no_visual_allowed))
        } else {
            val heigthModifier = Modifier.height(34.dp)
            HeadLigne(context, viewModel)
            StatisticPeriodic(context, viewModel)
            Spacer(modifier = heigthModifier)
            StatisticNonPeriodic(context, viewModel)
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
fun HeadLigne(context: Context, viewModel: HomeViewModel) {
    val periodList = Period.entries.map { it.getTitle(context) }
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd,
    ){
        Row {
            UnderlinedTextButton(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.see_more)
            ) {

            }
            Box(modifier = Modifier.width(200.dp)) {
                AppExposedDropdownMenu(
                    items = periodList,
                    defaultText = periodList[1]
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
fun StatisticPeriodic(context: Context, viewModel: HomeViewModel) {
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
                dataValue = totalAmountSalesState.toString(),
                devise = parameterState.devise
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
fun StatisticNonPeriodic(context: Context, viewModel: HomeViewModel) {
    val totalProductsState by viewModel.totalProducts.collectAsState()
    val totalAlertSeuilState by viewModel.totalAlertSeuilProducts.collectAsState()

    TitleMedium(
        title = stringResource(R.string.title_stat_no_period),
    )
    Row (
        modifier = Modifier.padding(top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ){
        val cardModifier = Modifier.weight(1f)
        ProductStatisticCard(modifier = cardModifier, dataValue = totalProductsState.toString())
        AlertInventoryStatisticCard(modifier = cardModifier, dataValue = totalAlertSeuilState.toString())
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
fun SaleStatisticCard(modifier: Modifier = Modifier, numberTitle: Int ? = null, dataValue: String ?, devise: String ? = null) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_sale,
        iconColor = Green,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise
    ) {

    }
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
fun ProductStatisticCard(modifier: Modifier = Modifier, numberTitle: Int ? = null, dataValue: String ?, devise: String ? = null) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_product,
        iconColor = Primary,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise
    ) {

    }
}

@Composable
fun AlertInventoryStatisticCard(modifier: Modifier = Modifier, numberTitle: Int ? = null, dataValue: String ?, devise: String ? = null) {
    StatisticCard(
        modifier = modifier,
        labelRes = R.string.statistic_label_alert_inventory,
        iconColor = Red,
        numberTitle = numberTitle,
        dataValue = dataValue,
        devise = devise
    ) {

    }
}