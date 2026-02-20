package com.groupec.salesb.core.domain.category

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.model.data.Category
import javax.inject.Inject

class GenerateCategoryListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        categories: List<Category>,
        searchQuery: String? = null
    ) = generateCategoryListExcel(context, categories, searchQuery)
}

fun generateCategoryListExcel(
    context: Context,
    categories: List<Category>,
    searchQuery: String?
): ByteArray {
    val none = "-"
    val headers = listOf(
        context.getString(R.string.id_header),
        context.getString(R.string.label),
        context.getString(R.string.description_header),
        context.getString(R.string.created_at_header),
        context.getString(R.string.username_header)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.category_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.category_list_total, categories.size)))
        add(emptyList())
        add(headers)
        categories.forEach { category ->
            add(
                listOf(
                    category.id?.toString() ?: none,
                    category.libelle,
                    category.description?.takeIf { it.isNotBlank() } ?: none,
                    category.datecreation?.dayMonthYear() ?: none,
                    category.username?.takeIf { it.isNotBlank() } ?: none
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
