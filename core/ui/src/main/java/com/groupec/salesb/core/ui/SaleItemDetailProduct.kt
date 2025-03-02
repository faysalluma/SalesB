package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.toDateString

@Composable
fun SaleItemDetailProduct(
    sale: Sale? = null,
    devise: String
) {
    sale?.let { s ->
        Column(modifier = Modifier.fillMaxWidth()) {
            TitleLarge(
                color = Primary,
                title = stringResource(
                    R.string.sale_item_dialog_title,
                    s.id ?: 0,
                    "${s.totalprix} $devise",
                    s.datevente?.toDateString(
                        format = "dd/MM/yyyy à HH:mm:ss"
                    )?:"",
                    s.username.toString()
                ),
                modifier = Modifier.padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )

            LazyColumn {
                item {
                    ProductSaleHeaderCard()
                }
                itemsIndexed(sale.details) { _, saleDetail ->
                    ProductSaleCard(saleDetail = saleDetail)
                }
            }
        }
    }
}