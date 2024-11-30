package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.groupec.salesb.core.model.data.Sale


@Composable
fun SaleCardList(sales: List<Sale>) {
    LazyColumn {
        items(sales) { sale ->
            Column(modifier = Modifier.fillMaxWidth()) {
                SaleCard(sale = sale)
            }
        }
    }
}