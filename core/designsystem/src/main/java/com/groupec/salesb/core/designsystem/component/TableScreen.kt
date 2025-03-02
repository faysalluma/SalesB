/*
package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun TableScreen(
) {
    LazyColumn(Modifier.padding(8.dp)) {
        item {

        }

        itemsIndexed(invoiceList) { _, invoice ->
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableCell(
                    text = invoice.invoice,
                    weight = column1Weight,
                    alignment = TextAlign.Left
                )
                TableCell(text = invoice.date, weight = column2Weight)
                StatusCell(text = invoice.status, weight = column3Weight)
                TableCell(
                    text = invoice.amount,
                    weight = column4Weight,
                    alignment = TextAlign.Right
                )
            }
            Divider(
                color = Color.LightGray,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxHeight()
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun TableRow(isTitle: Boolean = false) {

    val column1Weight = .2f
    val column2Weight = .3f
    val column3Weight = .25f
    val column4Weight = .25f

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TableCell(
            text = "Invoice",
            weight = column1Weight,
            alignment = TextAlign.Left,
            title = true
        )
        TableCell(text = "Date", weight = column2Weight, title = true)
        TableCell(text = "Status", weight = column3Weight, title = true)
        TableCell(
            text = "Amount",
            weight = column4Weight,
            alignment = TextAlign.Right,
            title = true
        )
    }
    Divider(
        color = Color.LightGray,
        modifier = Modifier
            .height(1.dp)
            .fillMaxHeight()
            .fillMaxWidth()
    )
}


@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
    title: Boolean = false
) {
    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(10.dp),
        fontWeight = if (title) FontWeight.Bold else FontWeight.Normal,
        textAlign = alignment,
    )
}

@Composable
fun RowScope.StatusCell(
    text: String,
    weight: Float,
    alignment: TextAlign = TextAlign.Center,
) {

    val color = when (text) {
        "Pending" -> Color(0xfff8deb5)
        "Paid" -> Color(0xffadf7a4)
        else -> Color(0xffffcccf)
    }
    val textColor = when (text) {
        "Pending" -> Color(0xffde7a1d)
        "Paid" -> Color(0xff00ad0e)
        else -> Color(0xffca1e17)
    }

    Text(
        text = text,
        Modifier
            .weight(weight)
            .padding(12.dp)
            .background(color, shape = RoundedCornerShape(50.dp)),
        textAlign = alignment,
        color = textColor
    )
}

data class Invoice(val invoice: String, val date: String, val status: String, val amount: String)

val invoiceList = listOf(
    Invoice("51023", "15/04/2023", "Unpaid", amount = "$2,600"),
    Invoice("51024", "17/04/2023", "Pending", amount = "$900"),
    Invoice("51025", "20/04/2023", "Paid", amount = "$7,560"),
    Invoice("51026", "23/04/2023", "Pending", amount = "$300"),
    Invoice("51027", "30/04/2023", "Paid", amount = "$5,890"),
)

@Preview
@Composable
fun TableScreenPreview() {
    TableScreen()
}
*/
