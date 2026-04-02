package com.groupec.salesb.core.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.groupec.salesb.core.model.data.Client

@Composable
fun ClientCardList(
    clients: LazyPagingItems<Client>,
    isSearching: Boolean,
    onViewDetail: (Client) -> Unit,
    onDelete: (Int, String) -> Unit,
    removeSelectedBgColor: Boolean
) {
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(removeSelectedBgColor) {
        selectedIndex = null
    }

    LazyColumn {
        itemsIndexed(clients) { index, client ->
            client?.let {
                val isSelected = index == selectedIndex
                ClientCard(
                    client = it,
                    isSelected = isSelected,
                    onViewDetail = {
                        selectedIndex = index
                        onViewDetail(it)
                    },
                    onDelete = onDelete
                )
            }
        }

        clients.apply {
            when (loadState.append) {
                is LoadState.Loading -> {
                    if (!isSearching) {
                        item { Text(stringResource(R.string.loading)) }
                    }
                }

                is LoadState.Error -> {
                    val error = loadState.append as LoadState.Error
                    item { Text("Error : ${error.error.message}") }
                }

                else -> {}
            }
        }
    }
}
