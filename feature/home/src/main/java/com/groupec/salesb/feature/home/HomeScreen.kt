package com.groupec.salesb.feature.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.Period
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleNormal
import com.groupec.salesb.core.designsystem.component.UnderlinedTextButton
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.Yellow
import com.groupec.salesb.core.ui.StatisticCard
import com.groupec.salesb.core.ui.StatisticChart

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    /*val userStoreState by viewModel.configUiState.collectAsState()
    Box {
        when (userStoreState) {
            is ConfigUiState.Loading -> LoadingScreen()
            is ConfigUiState.Configuration -> navigateToConfiguration()
            is ConfigUiState.Login -> navigateToLogin()
            else -> {}
        }
    }*/
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LeftDashBoard(modifier = Modifier.weight(0.6f))
        RightDashBoard(modifier = Modifier.weight(0.4f))
    }
}

@Composable
fun LeftDashBoard(modifier : Modifier = Modifier) {
    val context = LocalContext.current
    Column (modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val periodList = Period.entries.map { it.getTitle(context) }
            Row (
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                TitleLarge(
                    title = stringResource(R.string.title_stat),
                    modifier = Modifier.padding(top = 16.dp)
                )
                UnderlinedTextButton(
                    modifier = Modifier.padding(top = 8.dp),
                    text = stringResource(R.string.see_more)
                ) {

                }
            }

            AppExposedDropdownMenu(
                items = periodList,
                label = stringResource(R.string.label_select_period),
                defaultText = periodList[0]
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val cardModifier = Modifier.weight(1f)
        Row (
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SaleStatisticCard(modifier = cardModifier)
            ProductStatisticCard(modifier = cardModifier)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Row (
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ){
            EntrieStatisticCard(modifier = cardModifier)
            BenefitStatisticCard(modifier = cardModifier)
        }

        Spacer(modifier = Modifier.height(28.dp))

        StatisticChart()
    }
}

@Composable
fun RightDashBoard(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        TitleNormal(
            title = stringResource(R.string.title_last_sales),
            color = Green,
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        )
    }
}

@Composable
fun SaleStatisticCard(modifier: Modifier = Modifier) {
    StatisticCard(
        modifier = modifier,
        labelRes = com.groupec.salesb.core.ui.R.string.statistic_label_sale,
        iconColor = Green,
        value = 1026f
    ) {

    }
}

@Composable
fun ProductStatisticCard(modifier: Modifier = Modifier) {
    StatisticCard(
        modifier = modifier,
        labelRes = com.groupec.salesb.core.ui.R.string.statistic_label_product,
        iconColor = Yellow,
        value = 987f
    ) {

    }
}

@Composable
fun EntrieStatisticCard(modifier: Modifier = Modifier) {
    StatisticCard(
        modifier = modifier,
        labelRes = com.groupec.salesb.core.ui.R.string.statistic_label_entrie,
        iconColor = Red,
        value = 1026f
    ) {

    }
}

@Composable
fun BenefitStatisticCard(modifier: Modifier = Modifier) {
    StatisticCard(
        modifier = modifier,
        labelRes = com.groupec.salesb.core.ui.R.string.statistic_label_benefit,
        iconColor = Primary,
        value = 150f
    ) {

    }
}