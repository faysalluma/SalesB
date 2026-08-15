package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import com.groupec.salesb.core.model.data.Product
import androidx.paging.compose.LazyPagingItems
import com.groupec.salesb.core.model.data.Parameter

@Composable
fun ProductGridAdaptive(
    products: LazyPagingItems<Product>,
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: MutableMap<Int, Boolean>,
    isSearching: Boolean,
    parameter: Parameter
) {
    val columns = GridCells.Adaptive(minSize = 124.dp)
    LazyVerticalGrid(
        columns = columns, // Taille minimale pour chaque élément (calcule le nombre de colonnes selon la largeur disponible)
        contentPadding = PaddingValues(bottom = 8.dp), // marge interieur autour de l'ensemble de la grille
        horizontalArrangement = Arrangement.spacedBy(16.dp), // espace entre les lignes de la grille (verticalement)
        verticalArrangement = Arrangement.spacedBy(16.dp), //  espace entre les colonnes de la grille (horizontalement)
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        items(products.itemCount) { index ->
            products[index]?.let { product ->
                val isChecked = selectedProducts.contains(Pair(product.id, product))
                ProductGridItem(
                    product = product,
                    isChecked = isChecked,
                    parameter = parameter,
                    onclick = {
                        if (isChecked) {
                            selectedProducts.remove(Pair(product.id, product))
                            textFieldValues.remove(product.id)
                            quantityCheck.remove(product.id)
                        } else {
                            val productId = product.id ?:0
                            selectedProducts.add(productId to product)
                            textFieldValues[productId] = "1.0"
                            quantityCheck[productId] = false
                        }
                    }
                )
            }
        }

        products.apply {
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
