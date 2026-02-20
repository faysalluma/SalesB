package com.groupec.salesb.core.domain.user

import android.content.Context
import com.groupec.salesb.core.currentLocalDateString
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.domain.R
import com.groupec.salesb.core.model.data.User
import javax.inject.Inject

class GenerateUserListExcelUseCase @Inject constructor() {
    operator fun invoke(
        context: Context,
        users: List<User>,
        searchQuery: String? = null
    ) = generateUserListExcel(context, users, searchQuery)
}

fun generateUserListExcel(
    context: Context,
    users: List<User>,
    searchQuery: String?
): ByteArray {
    val none = "-"
    val headers = listOf(
        context.getString(R.string.id_header),
        context.getString(R.string.name_header),
        context.getString(R.string.email_header),
        context.getString(R.string.phone_header),
        context.getString(R.string.address_header),
        context.getString(R.string.active_header),
        context.getString(R.string.created_at_header)
    )

    val rows = buildList {
        add(listOf(context.getString(R.string.user_list_title)))
        add(listOf("${context.getString(R.string.export_date)} ${currentLocalDateString()}"))
        if (!searchQuery.isNullOrBlank()) {
            add(listOf("${context.getString(R.string.filter_data)} $searchQuery"))
        }
        add(listOf(context.getString(R.string.user_list_total, users.size)))
        add(emptyList())
        add(headers)
        users.forEach { user ->
            add(
                listOf(
                    user.id?.toString() ?: none,
                    user.nomprenom,
                    user.email,
                    user.tel?.takeIf { it.isNotBlank() } ?: none,
                    user.adresse?.takeIf { it.isNotBlank() } ?: none,
                    if (user.actif) context.getString(R.string.yes) else context.getString(R.string.no),
                    user.datecreation?.dayMonthYear() ?: none
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
