package com.groupec.salesb.core.domain.rayon

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.model.data.Rayon
import javax.inject.Inject

class GenerateRayonListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        rayons: List<Rayon>,
        searchQuery: String? = null
    ) = generateRayonListExcel(context, rayons, searchQuery)
}

fun generateRayonListExcel(
    context: Context,
    rayons: List<Rayon>,
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
        add(listOf(context.getString(R.string.rayon_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.rayon_list_total, rayons.size)))
        add(emptyList())
        add(headers)
        rayons.forEach { rayon ->
            add(
                listOf(
                    rayon.id?.toString() ?: none,
                    rayon.libelle,
                    rayon.description?.takeIf { it.isNotBlank() } ?: none,
                    rayon.datecreation?.dayMonthYear() ?: none,
                    rayon.username?.takeIf { it.isNotBlank() } ?: none
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
