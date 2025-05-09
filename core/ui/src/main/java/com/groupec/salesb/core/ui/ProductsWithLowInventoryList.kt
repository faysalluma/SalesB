package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.model.data.Product

@Composable
fun ProductsWithLowInventoryList(
    products: List<Product>? = null
) {
    products?.let { productsValue ->
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TitleLarge(
                title = stringResource(R.string.low_inventory_products),
                modifier = Modifier.padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )

            LazyColumn {
                item {
                    ProductWithLowInventoryHeaderCard()
                }
                items(productsValue) { product ->
                    ProductWithLowInventoryCard(product = product)
                }
            }
        }
    }
}