package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.groupec.salesb.core.model.data.Product


@Composable
fun ProductWithLowInventoryCard(
    product: Product
) {
    ProductWithLowInventoryTableRow(product = product)
}

@Composable
fun ProductWithLowInventoryHeaderCard() {
    ProductWithLowInventoryTableRow(isTitle = true)
}

@Composable
fun ProductWithLowInventoryTableRow(
    product: Product? = null,
    isTitle: Boolean = false
) {
    val column1Weight = .1f
    val column2Weight = .1f

    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.weight(1f)) {
            TableCell(
                text = product?.libelle ?: stringResource(R.string.label),
                weight = column1Weight,
                isTitle = isTitle
            )
            TableCell(
                text = product?.qtestock?.toString() ?: stringResource(R.string.quantity),
                weight = column2Weight,
                isTitle = isTitle
            )
        }
    }
    HorizontalDivider()
}
