package com.groupec.salesb.core.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.groupec.salesb.core.model.data.Product
import androidx.paging.compose.items


@Composable
fun ProductCardList(
    products: LazyPagingItems<Product>,
    isSearching: Boolean,
    onViewDetail: (Product) -> Unit,
    onDelete: (Int) -> Unit
) {
    // Liste paginée
    LazyColumn {
        items(products) { product ->
            product?.let {
                ProductCard(it, onViewDetail, onDelete)
            }
        }

        products.apply {
            when (loadState.append) {
                is LoadState.Loading -> {
                    if (!isSearching) {
                        item { Text("Chargement...") }
                    }
                }

                is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item { Text("Erreur : ${e.error.message}") }
                }

                else -> {}
            }
        }
    }
}