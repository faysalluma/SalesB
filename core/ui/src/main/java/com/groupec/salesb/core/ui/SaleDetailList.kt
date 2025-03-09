package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.groupec.salesb.core.model.data.Product

@Composable
fun SaleDetailList(
    selectedProducts: List<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
    devise: String,
    quantityCheck: Map<Int, Boolean>
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState()),
    ) {
        selectedProducts.forEach { productLine ->
            SaleDetailListItem(productLine, textFieldValues, onQuantityChange, devise, quantityCheck)
        }
    }
}