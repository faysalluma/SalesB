package com.groupec.salesb.core.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.print.PrintAction

@Composable
fun SaleCardList(
    sales: LazyPagingItems<Sale>,
    isSearching: Boolean,
    parameter: Parameter,
    onViewDetail: (Sale) -> Unit,
    onPrintOrShare: (Sale, PrintAction) -> Unit,
) {

    // Liste paginée
    LazyColumn {
        item {
            SaleHeaderCard()
        }

        itemsIndexed(sales) { index, sale ->
            sale?.let {
                SaleCard(
                    parameter = parameter,
                    sale = it,
                    onViewDetail = onViewDetail,
                    onPrintOrShare = onPrintOrShare
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