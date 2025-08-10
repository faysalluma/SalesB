package com.groupec.salesb.core.ui

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Primary
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line


@Composable
fun StatisticChart(modifier: Modifier = Modifier, values: List<Pair<String, Double>>) {
    val  context = LocalContext.current
    LineChart(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp),
        data = listOf(
            Line(
                label = context.getString(R.string.statistic_label_chart),
                values = values.map { it.second },
                color = SolidColor(Primary),
                firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                secondGradientFillColor = Color.Transparent,
                strokeAnimationSpec = tween(1500, easing = EaseInOutCubic),
                gradientAnimationDelay = 1000,
                drawStyle = DrawStyle.Stroke(width = 2.dp),
            )
        ),
        animationMode = AnimationMode.Together(delayBuilder = {
            it * 500L
        }),
        labelProperties = LabelProperties(
            enabled = true, // Activer les labels pour l'axe X
            labels = values.map { it.first }, // Vos étiquettes pour l'axe X
           /* textStyle = TextStyle(
                color = Color.Black,
                fontSize = 12.sp
            )*/
        )
    )
}

/*ColumnChart(
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
    )*/

@Preview
@Composable
fun StatisticChartPreview() {
    StatisticChart(values = listOf(
        "Jan" to 30.8,
        "Fev" to 41.0,
        "Mars" to 8.2,
        "Avril" to 10.8,
        "Mai" to 35.0,
    ))
}
