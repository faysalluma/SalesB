package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.groupec.salesb.core.model.data.Sale

@Composable
fun SaleCardList(
    sales: LazyPagingItems<Sale>,
    isSearching: Boolean,
    onViewDetail: (Sale) -> Unit,
    onDelete: (Int, String) -> Unit,
) {

    // Liste paginée
    LazyColumn {
        item {
            SaleHeaderCard()
        }

        itemsIndexed(sales) { index, sale ->
            sale?.let {
                SaleCard(
                    sale = it,
                    onViewDetail = onViewDetail,
                    onDelete = onDelete
                )
            }
        }

        sales.apply {
            when (loadState.append) {
                is LoadState.Loading -> {
                    if (!isSearching) {
                        item { Text(stringResource(R.string.loading)) }
                    }
                }

                is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item { Text("Error : ${e.error.message}") }
                }

                else -> {}
            }
        }
    }
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