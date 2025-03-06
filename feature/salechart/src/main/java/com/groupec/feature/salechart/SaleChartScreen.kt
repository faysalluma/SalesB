package com.groupec.feature.salechart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.convertToViewDateFormat
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.StatisticChart

@Composable
fun SaleChartScreen(
    modifier: Modifier = Modifier,
    startDate: String,
    endDate: String,
    viewModel: SaleChartViewModel = hiltViewModel(),
) {
    val chartValuesState by viewModel.chartValues.collectAsState()

    ComposableLifecycle(
        onCreate = {
            viewModel.getChartDataByDate(startDate, endDate)
        }
    )

    Column(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TitleLarge(
            color = Primary,
            title = stringResource(
                R.string.sales_chart_title,
                startDate.convertToViewDateFormat(),
                endDate.convertToViewDateFormat()
            ),
            modifier = Modifier.padding(vertical = 16.dp),
            textAlign = TextAlign.Center
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (chartValuesState.isNotEmpty()) {
                StatisticChart(values = chartValuesState)
            } else {
                Text(stringResource(R.string.no_data_chart))
            }
        }
    }
}