package com.groupec.salesb.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.ProductImage
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.component.TitleSmall
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Invoicing
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.toDate
import com.groupec.salesb.core.toPercentFormat
import com.groupec.salesb.core.toWordsWithIcuRespectingLocaleAndCurrency

@Composable
fun InvoiceContent(
    sale: Sale,
    parameter: Parameter,
    invoicing: Invoicing
) {
    val spacerZone = 22.dp
    val totalSale = sale.details.map { it.qte * it.prix }
        .reduce { acc, value -> acc + value }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(spacerZone)
    ) {
        HeaderSection(parameter, invoicing)
        BillingInfo(parameter, sale)
        PaymentSummary(parameter, totalSale)
        // FooterNotice(parameter, totalSale)
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InvoiceAction(
    modifier: Modifier = Modifier,
    onChangeInvoiceData: () -> Unit,
    onDownload: () -> Unit,
    onPrint: () -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(top = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DefaultButton(
                modifier = Modifier
                    .wrapContentSize(),
                containerColor = Silver,
                border = BorderStroke(1.dp, Silver),
                textcolor = Color.Black,
                text = stringResource(R.string.change_invoice_info),
                onClick = onChangeInvoiceData
            )
            DefaultButton(
                modifier = Modifier
                    .wrapContentSize(),
                text = stringResource(R.string.download_invoice),
                onClick = onDownload
            )
            DefaultButton(
                modifier = Modifier
                    .wrapContentSize(),
                text = stringResource(R.string.print_invoice_a4),
                onClick = onPrint
            )
        }
    }

}

@Composable
fun HeaderSection(parameter: Parameter, invoicing: Invoicing) {
    val context = LocalContext.current
    Column {
        parameter.logo?.takeIf { it.isNotBlank() }?.let { logoUrl ->
            ProductImage(
                modifier = Modifier
                    .width(150.dp)
                    .height(60.dp),
                url = logoUrl,
                contentDescription = "Client logo"
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f).padding(8.dp)
            ) {
                Text(text = parameter.raisonsociale)

                parameter.adresse?.takeIf { it.isNotEmpty() } ?.let {
                    Text(
                        text = it,
                        maxLines = 5,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                parameter.email?.takeIf { it.isNotEmpty() } ?.let {
                    Text(text = it)
                }

                parameter.telephone?.takeIf { it.isNotEmpty() } ?.let {
                    Text(text = it)
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = stringResource(com.groupec.salesb.core.R.string.billing_address),
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = invoicing.fullName.uppercase(),
                    fontStyle = FontStyle.Italic,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = invoicing.address.uppercase(),
                    fontStyle = FontStyle.Italic,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BillingInfo(parameter: Parameter, sale: Sale) {
    val context = LocalContext.current
    val paidByValue = sale.paymenttype
        ?.takeIf { it.isNotBlank() }
        ?.let { paymentType ->
            paymentTypeLibelleResFromValue(paymentType)?.let(context::getString) ?: paymentType
        }

    Column {
        // Head
        sale.datevente?.convertToLocaleDateTimeFormat()?.let {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)){
                        append(stringResource(com.groupec.salesb.core.R.string.date_of_checkout))
                    }
                    append(" $it")
                }
            )

        }
        sale.id?.let {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(stringResource(com.groupec.salesb.core.R.string.invoice_no))
                    }
                    append(" $it")
                }
            )
        }
        Text(
            text = buildAnnotatedString {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                    append(stringResource(com.groupec.salesb.core.R.string.invoice_date))
                }
                append(" ${currentLocalDateString()}")
            }
        )
        paidByValue?.let {
            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Medium)) {
                        append(stringResource(com.groupec.salesb.core.R.string.paid_by_no_param))
                    }
                    append( " $it")
                }
            )
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Detail products
        LazyColumn {
            item {
                ProductHeaderPrintCard()
            }
            itemsIndexed(sale.details) { _, saleDetail ->
                ProductPrintCard(saleDetail = saleDetail)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TitleMedium(title = stringResource(com.groupec.salesb.core.R.string.invoice_summary, sale.details.size))
            TitleSmall(stringResource(com.groupec.salesb.core.R.string.infos_tva, parameter.tva.toPercentFormat()))
        }
    }
}

@Composable
fun PaymentSummary(parameter: Parameter, totalSale: Double) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TitleMedium(title = stringResource(
            R.string.total_sales,
            totalSale.formatAmount() + " " + parameter.devise
         )
        )
    }
}

@Composable
fun FooterNotice(parameter: Parameter, totalSale: Double) {
    Text(
        modifier = Modifier.padding(bottom = 22.dp),
        text = stringResource(
            com.groupec.salesb.core.R.string.etablish_invoice_message,
            totalSale.toWordsWithIcuRespectingLocaleAndCurrency(
                mainUnit = parameter.devise.lowercase(),
                subUnit = if (parameter.devise.contains("eur", ignoreCase = true)) {
                    "centime"
                } else {
                    null
                }
            )
        ),
        style = MaterialTheme.typography.bodySmall
    )
}

@Preview
@Composable
fun InvoiceContentPreview() {
    InvoiceContent(
        sale = Sale(
            1,
            "2025-07-17 10:22".toDate(),
            25.30,
            details = listOf(
                SaleDetail(1, "Sucre", 2.30, 10.0),
                SaleDetail(2, "Lait", 3.0, 5.0)
            )
        ),
        parameter = Parameter(devise = "FCFA", raisonsociale = "GROUPE C", adresse = "Cotonou"),
        invoicing = Invoicing("SANDA Faysal", "147 rue basse, 14000 Caen")
    )
}
