package com.groupec.salesb.core.domain.client

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.model.data.Client
import javax.inject.Inject

class GenerateClientListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        clients: List<Client>,
        searchQuery: String? = null
    ) = generateClientListExcel(context, clients, searchQuery)
}

fun generateClientListExcel(
    context: Context,
    clients: List<Client>,
    searchQuery: String?
): ByteArray {
    val none = "-"
    val headers = listOf(
        context.getString(R.string.id_header),
        context.getString(R.string.name_header),
        context.getString(R.string.address_header),
        context.getString(R.string.phone_header),
        context.getString(R.string.created_at_header),
        context.getString(R.string.username_header)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.client_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.client_list_total, clients.size)))
        add(emptyList())
        add(headers)
        clients.forEach { client ->
            add(
                listOf(
                    client.id?.toString() ?: none,
                    client.nomprenom,
                    client.adresse?.takeIf { it.isNotBlank() } ?: none,
                    client.telephone?.takeIf { it.isNotBlank() } ?: none,
                    client.datecreation?.dayMonthYear() ?: none,
                    client.username?.takeIf { it.isNotBlank() } ?: none
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
