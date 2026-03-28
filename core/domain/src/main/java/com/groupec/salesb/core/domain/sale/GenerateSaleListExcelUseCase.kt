package com.groupec.salesb.core.domain.sale

import android.content.Context
import com.groupec.salesb.core.convertToLocaleDateTimeFormat
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.others.paymentTypeLibelleResFromValue
import com.groupec.salesb.core.toDate
import javax.inject.Inject

class GenerateSaleListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        sales: List<Sale>,
        searchQuery: String? = null,
        startDate: String? = null,
        endDate: String? = null,
        devise: String? = null,
        isServiceView: Boolean = false
    ) = generateSaleListExcel(context, sales, searchQuery, startDate, endDate, devise, isServiceView)
}

fun generateSaleListExcel(
    context: Context,
    sales: List<Sale>,
    searchQuery: String?,
    startDate: String?,
    endDate: String?,
    devise: String?,
    isServiceView: Boolean = false
): ByteArray {
    val none = "-"
    val catalogLabelPlural = context.getCatalogItemLabel(
        isServiceView = isServiceView,
        plural = true,
        capitalize = true
    )
    val headers = listOf(
        //context.getString(R.string.id_header),
        context.getString(R.string.date_time_header),
        context.getString(R.string.price_header),
        context.getString(R.string.payment_type_header),
        context.getString(R.string.username_header),
        context.getString(R.string.products_header, catalogLabelPlural)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.sale_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))

        buildSaleFilterLines(context, searchQuery, startDate, endDate).forEach { filterLine ->
            add(listOf("${context.getString(R.string.filter_data)} $filterLine"))
        }

        add(listOf(context.getString(R.string.sale_list_total, sales.size)))
        add(emptyList())
        add(headers)
        sales.forEach { sale ->
            val products = if (sale.details.isNotEmpty()) {
                sale.details.joinToString(", ") { detail ->
                    val label = detail.libelle?.takeIf { it.isNotBlank() } ?: none
                    "${detail.qte} x $label"
                }
            } else {
                none
            }

            add(
                listOf(
                    //sale.id?.toString() ?: none,
                    sale.datevente?.convertToLocaleDateTimeFormat()?.replace(" - ", " : ") ?: none,
                    sale.totalprix.formatAmount(),
                    sale.paymenttype
                        ?.takeIf { it.isNotBlank() }
                        ?.let { value ->
                            paymentTypeLibelleResFromValue(value)?.let { context.getString(it) } ?: value
                        } ?: none,
                    sale.username?.takeIf { it.isNotBlank() } ?: none,
                    products
                )
            )
        }
    }

    val csv = rows.joinToString("\n") { row ->
        row.joinToString(",") { cell ->
            val escaped = cell.replace("\"", "\"\"")
            "\"$escaped\""
        }
    }

    // UTF-8 BOM improves Excel compatibility for accents.
    return "\uFEFF$csv".toByteArray(Charsets.UTF_8)
}

private fun buildSaleFilterLines(
    context: Context,
    searchQuery: String?,
    startDate: String?,
    endDate: String?
): List<String> {
    val filters = mutableListOf<String>()
    if (!searchQuery.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_amount, searchQuery))
    }
    if (!startDate.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_start_date, startDate.toLocaleDate()))
    }
    if (!endDate.isNullOrBlank()) {
        filters.add(context.getString(R.string.sale_filter_end_date, endDate.toLocaleDate()))
    }
    return filters
}

private fun String.toLocaleDate(): String {
    return toDate(format = "yyyy-MM-dd")?.convertToLocaleDateTimeFormat(excludeTime = true) ?: this
}

private fun Double.formatAmountWithCurrency(devise: String?): String {
    val amount = formatAmountNoTrailingZero()
    return if (devise.isNullOrBlank()) amount else "$amount $devise"
}

private fun Double.formatAmountNoTrailingZero(): String {
    val formatted = formatAmount()
    return formatted.replace(Regex("([.,])00$"), "")
}
