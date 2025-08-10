package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DataTableScreen() {
    var rows by remember {
        mutableStateOf(
            listOf(
                TableRow(1, "Alice", 25),
                TableRow(2, "Bob", 30),
                TableRow(3, "Charlie", 22)
            )
        )
    }
    var sortAscending by remember { mutableStateOf(true) }
    var selectedRows by remember { mutableStateOf(setOf<Int>()) }

    Column {
        // Header with sorting
        Row(modifier = Modifier.fillMaxWidth()) {
            TableHeader("ID", Modifier.weight(1f)) {
                rows = rows.sortedBy { it.id * if (sortAscending) 1 else -1 }
                sortAscending = !sortAscending
            }
            TableHeader("Name", Modifier.weight(3f)) {
                rows = rows.sortedBy { if (sortAscending) it.name else it.name.reversed() }
                sortAscending = !sortAscending
            }
            TableHeader("Age", Modifier.weight(1f)) {
                rows = rows.sortedBy { it.age * if (sortAscending) 1 else -1 }
                sortAscending = !sortAscending
            }
        }

        // Rows
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(rows) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedRows.contains(row.id)) {
                                selectedRows = selectedRows - row.id
                            } else {
                                selectedRows = selectedRows + row.id
                            }
                        }
                        .background(
                            if (selectedRows.contains(row.id)) Color.LightGray else Color.Transparent
                        )
                        .padding(8.dp)
                ) {
                    Checkbox(
                        checked = selectedRows.contains(row.id),
                        onCheckedChange = {
                            if (it) selectedRows = selectedRows + row.id
                            else selectedRows = selectedRows - row.id
                        }
                    )
                    Text(row.id.toString(), Modifier.weight(1f))
                    Text(row.name, Modifier.weight(3f))
                    Text(row.age.toString(), Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun TableHeader(title: String, modifier: Modifier = Modifier, onSort: () -> Unit) {
    Text(
        text = title,
        modifier = modifier
            .clickable { onSort() }
            .padding(8.dp),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}

data class TableRow(
    val id: Int,
    val name: String,
    val age: Int,
    val selected: Boolean = false
)

@Preview
@Composable
fun DataTableScreenPreview() {
    DataTableScreen()
}
