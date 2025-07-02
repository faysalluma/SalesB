package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.model.data.SaleDetail


@Composable
fun ProductSaleCard(
    saleDetail: SaleDetail
) {
    ProductTableRow(saleDetail = saleDetail)
}

@Composable
fun ProductSaleHeaderCard() {
    ProductTableRow(isTitle = true)
}

@Composable
fun ProductTableRow(
    saleDetail: SaleDetail? = null,
    isTitle: Boolean = false
) {
    val column1Weight = .15f
    val column2Weight = .3f
    val column3Weight = .25f
    val column4Weight = .3f

    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.weight(1f)) {
            TableCell(
                text = saleDetail?.id?.toString() ?: "Id",
                weight = column1Weight,
                alignment = TextAlign.Left,
                isTitle = isTitle
            )
            TableCell(
                text = saleDetail?.libelle ?: stringResource(R.string.label),
                weight = column2Weight,
                isTitle = isTitle
            )
            TableCell(
                text = saleDetail?.prix?.toString() ?: stringResource(R.string.price),
                weight = column3Weight,
                isTitle = isTitle
            )
            TableCell(
                text = saleDetail?.qte?.toString() ?: stringResource(R.string.quantity),
                weight = column4Weight,
                isTitle = isTitle
            )
        }
        // Additional code
    }
    HorizontalDivider()
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun ProductSaleCardPreview() {
    SalesBAppTheme {
        Column {
            ProductSaleCardPreview()
            ProductSaleCard(saleDetail = SaleDetail(1, "P1", 2.0, 1.0))
        }

    }
}