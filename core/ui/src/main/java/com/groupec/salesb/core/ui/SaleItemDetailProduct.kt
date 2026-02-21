package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue

@Composable
fun SaleItemDetailProduct(
    sale: Sale? = null,
    parameter: Parameter
) {
    val context = LocalContext.current
    sale?.let { s ->
        Column(modifier = Modifier.fillMaxWidth()) {
            TitleLarge(
                color = Primary,
                title = stringResource(
                    R.string.sale_item_dialog_title,
                    s.id ?: 0,
                    "${s.totalprix.formatAmount()} ${parameter.devise}",
                    s.datevente?.convertToLocaleDateTimeFormat(
                    )?:"",
                    s.username.toString()
                ),
                modifier = Modifier.padding(vertical = 12.dp),
                textAlign = TextAlign.Center
            )

            val paidByValue = sale.paymenttype
                ?.takeIf { it.isNotBlank() }
                ?.let { paymentType ->
                    paymentTypeLibelleResFromValue(paymentType)?.let(context::getString) ?: paymentType
                }
            paidByValue?.let {
                Text(
                    text = stringResource(R.string.paid_by, paidByValue),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

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
