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
import com.groupec.salesb.core.model.data.Category


@Composable
fun CategoryCardList(
    categories: LazyPagingItems<Category>,
    isSearching: Boolean,
    onViewDetail: (Category) -> Unit,
    onDelete: (Int, String) -> Unit,
    removeSelectedBgColor: Boolean
) {
    // Track selected item index
    var selectedIndex by remember { mutableStateOf<Int?>(null) }

    // Remove selected background color when click on Cancel from ProductListScreen
    LaunchedEffect(removeSelectedBgColor) {
        selectedIndex = null
    }

    // Liste paginée
    LazyColumn {
        itemsIndexed(categories){ index, category ->
            category?.let {
                val isSelected = index == selectedIndex // Check if item is selected
                CategoryCard(
                    category = it,
                    isSelected = isSelected,
                    onViewDetail = {
                        selectedIndex = index
                        onViewDetail(it)
                    },
                    onDelete = onDelete
                )
            }
        }

        categories.apply {
            when (loadState.append) {
                is LoadState.Loading -> {
                    if (!isSearching) {
                        item { Text(stringResource(R.string.loading)) }
                    }
                }

                is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item { Text("Error : ${e.error.message}") }
                }

                else -> {}
            }
        }
    }
}