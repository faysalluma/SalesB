package com.groupec.salesb.core.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars


@Composable
fun StatisticChart(modifier: Modifier = Modifier) {
    val  context = LocalContext.current
    ColumnChart(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        data = remember {
            listOf(
                Bars(
                    label = "Jan",
                    values = listOf(
                        Bars.Data(
                            label = context.getString(R.string.statistic_label_sale),
                            value = 50.0,
                            color = SolidColor(Green)
                        ),
                        Bars.Data(
                            label = context.getString(R.string.statistic_label_benefit),
                            value = 70.0,
                            color = SolidColor(Primary)
                        )
                    ),
                ),
                Bars(
                    label = "Feb",
                    values = listOf(
                        Bars.Data(
                            label = context.getString(R.string.statistic_label_sale),
                            value = 80.0,
                            color = SolidColor(Green)
                        ),
                        Bars.Data(
                            label = context.getString(R.string.statistic_label_benefit),
                            value = 60.0,
                            color = SolidColor(Primary)
                        )
                    ),
                )
            )
        },
        barProperties = BarProperties(
            cornerRadius = Bars.Data.Radius.Rectangle(topRight = 6.dp, topLeft = 6.dp),
            spacing = 3.dp,
            // style = DrawStyle.Stroke(10.dp)
        ),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
    )
}