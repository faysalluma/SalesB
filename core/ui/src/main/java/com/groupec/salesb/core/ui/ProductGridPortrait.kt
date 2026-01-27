package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.ProductImage
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.icon.AppIcons.CheckCircle
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Product


@Composable
fun ProductGridPortrait(
    products: LazyPagingItems<Product>,
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    isSearching: Boolean,
    devise: String?,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 124.dp),
        contentPadding =  PaddingValues(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        items(products.itemCount) { index ->
            products[index]?.let { product ->
                val productId = product.id ?: 0
                val quantity = textFieldValues[productId]?.toDoubleOrNull() ?: 0.0
                ProductGridPortraitItem(
                    product = product,
                    quantity = quantity,
                    devise = devise,
                    onAdd = {
                        val nextQuantity = quantity + 1.0
                        val stockLimit = product.qtestock?.let { nextQuantity > it } ?: false
                        if (selectedProducts.none { it.first == productId }) {
                            selectedProducts.add(productId to product)
                        }
                        textFieldValues[productId] = nextQuantity.autoRound()
                        onQuantityChange(productId to product)
                    },
                    onRemove = {
                        if (quantity > 0) {
                            val nextQuantity = quantity - 1.0
                            textFieldValues[productId] = nextQuantity.autoRound()
                            onQuantityChange(productId to product)
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

@Composable
private fun ProductGridPortraitItem(
    product: Product,
    quantity: Double,
    devise: String?,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row {
                ProductImage(url = product.image?.let { Constants.UPLOAD_URL.plus(it) })
                if (quantity > 0) {
                    Icon(
                        imageVector = CheckCircle,
                        contentDescription = "Selected",
                        tint = Primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.libelle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${product.prixttc} $devise",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall
            )
            Row(
               modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onRemove,
                    enabled = quantity > 0
                ) {
                    Icon(
                        imageVector = AppIcons.MinusCircleOutline,
                        contentDescription = "Minus quantity",
                        tint = if (quantity > 0) Primary else Silver
                    )
                }
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = quantity.toString(),
                    style = MaterialTheme.typography.titleSmall
                )
                IconButton(onClick = onAdd) {
                    Icon(
                        imageVector = AppIcons.AddCircleOutline,
                        contentDescription = "Add quantity",
                        tint = Primary
                    )
                }
            }
            product.qtestock?.let {
                if (quantity > it) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleSmall,
                        text = stringResource(R.string.quantity_greater),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
