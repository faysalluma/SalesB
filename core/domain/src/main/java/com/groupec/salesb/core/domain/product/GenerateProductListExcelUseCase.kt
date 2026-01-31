package com.groupec.salesb.core.domain.product

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Product
import javax.inject.Inject

class GenerateProductListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        products: List<Product>,
        searchQuery: String? = null
    ) = generateProductListExcel(context, products, searchQuery)
}

fun generateProductListExcel(
    context: Context,
    products: List<Product>,
    searchQuery: String?
): ByteArray {
    val none = "-"
    val headers = listOf(
        context.getString(R.string.product_id_header),
        context.getString(R.string.label),
        context.getString(R.string.reference_header),
        context.getString(R.string.description_header),
        context.getString(R.string.price_ht_header),
        context.getString(R.string.price_ttc_header),
        context.getString(R.string.stock_header),
        context.getString(R.string.stock_min_header),
        context.getString(R.string.category_header),
        context.getString(R.string.rayon_header),
        context.getString(R.string.supplier_header),
        context.getString(R.string.created_at_header),
        context.getString(R.string.updated_at_header),
        context.getString(R.string.username_header)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.product_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.product_list_total, products.size)))
        add(emptyList())
        add(headers)
        products.forEach { product ->
            add(
                listOf(
                    product.id?.toString() ?: none,
                    product.libelle,
                    product.reference?.takeIf { it.isNotBlank() } ?: none,
                    product.description?.takeIf { it.isNotBlank() } ?: none,
                    product.prixht?.formatAmount() ?: none,
                    product.prixttc.formatAmount(),
                    product.qtestock?.toString() ?: none,
                    product.stockmini?.toString() ?: none,
                    product.categorielibelle?.takeIf { it.isNotBlank() } ?: none,
                    product.rayonlibelle?.takeIf { it.isNotBlank() } ?: none,
                    product.fournisseurlibelle?.takeIf { it.isNotBlank() } ?: none,
                    product.datecreation?.dayMonthYear() ?: none,
                    product.datemodif?.dayMonthYear() ?: none,
                    product.username?.takeIf { it.isNotBlank() } ?: none
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

    return csv.toByteArray(Charsets.UTF_8)
}
