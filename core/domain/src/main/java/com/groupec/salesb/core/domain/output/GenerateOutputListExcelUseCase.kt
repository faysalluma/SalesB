package com.groupec.salesb.core.domain.output

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.model.data.Output
import javax.inject.Inject

class GenerateOutputListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        outputs: List<Output>,
        searchQuery: String? = null
    ) = generateOutputListExcel(context, outputs, searchQuery)
}

fun generateOutputListExcel(
    context: Context,
    outputs: List<Output>,
    searchQuery: String?
): ByteArray {
    val none = "-"
    val headers = listOf(
        context.getString(R.string.id_header),
        context.getString(R.string.description_header),
        context.getString(R.string.price_header),
        context.getString(R.string.created_at_header),
        context.getString(R.string.username_header)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.output_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.output_list_total, outputs.size)))
        add(emptyList())
        add(headers)
        outputs.forEach { output ->
            add(
                listOf(
                    output.id?.toString() ?: none,
                    output.description,
                    output.prix.formatAmount(),
                    output.datecreation?.dayMonthYear() ?: none,
                    output.username?.takeIf { it.isNotBlank() } ?: none
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
