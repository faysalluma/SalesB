package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.autoRound
import com.groupec.salesb.core.designsystem.component.ProductImage
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.icon.AppIcons.CheckCircle
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Parameter
import com.groupec.salesb.core.model.data.Product


@Composable
fun ProductGridPortrait(
    products: LazyPagingItems<Product>,
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    isSearching: Boolean,
    parameter: Parameter,
    onQuantityChange: (Pair<Int, Product>) -> Unit,
) {
    val isIntegerQuantityMode = parameter.serviceview || parameter.useintforpriceandamout
    val itemBottomSpace = 96.dp
    if (parameter.serviceview) {
        LazyColumn {
            itemsIndexed(products) { _, product ->
                product?.let { product ->
                    val productId = product.id ?: 0
                    val quantity = textFieldValues[productId]?.toIntOrNull()
                        ?: textFieldValues[productId]?.toDoubleOrNull()?.toInt()
                        ?: 0
                    ProductGridServicePortraitItem(
                        product = product,
                        quantity = quantity,
                        parameter = parameter,
                        onAdd = {
                            val nextQuantity = quantity + 1
                            if (selectedProducts.none { it.first == productId }) {
                                selectedProducts.add(productId to product)
                            }
                            textFieldValues[productId] = nextQuantity.toString()
                            onQuantityChange(productId to product)
                        },
                        onRemove = {
                            if (quantity > 0) {
                                val nextQuantity = quantity - 1
                                textFieldValues[productId] = nextQuantity.toString()
                                onQuantityChange(productId to product)
                            }
                        }
                    )
                }
            }

            // Additional space for the resume card
            item {
                Spacer(modifier = Modifier.height(itemBottomSpace))
            }
        }
    } else {
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
                    val quantity = if (isIntegerQuantityMode) {
                        textFieldValues[productId]?.toIntOrNull()?.toDouble() ?: 0.0
                    } else {
                        textFieldValues[productId]?.toDoubleOrNull() ?: 0.0
                    }
                    ProductGridMerchantPortraitItem(
                        product = product,
                        quantity = quantity,
                        isIntegerQuantityMode = isIntegerQuantityMode,
                        parameter = parameter,
                        onAdd = {
                            if (selectedProducts.none { it.first == productId }) {
                                selectedProducts.add(productId to product)
                            }
                            textFieldValues[productId] = if (isIntegerQuantityMode) {
                                (quantity.toInt() + 1).toString()
                            } else {
                                (quantity + 1.0).autoRound()
                            }
                            onQuantityChange(productId to product)
                        },
                        onRemove = {
                            if (quantity > 0) {
                                textFieldValues[productId] = if (isIntegerQuantityMode) {
                                    (quantity.toInt() - 1).toString()
                                } else {
                                    (quantity - 1.0).autoRound()
                                }
                                onQuantityChange(productId to product)
                            }
                        }
                    )
                }
            }

            // Additional space for the resume card
            item (span = { GridItemSpan(maxLineSpan) }) {
                Spacer(modifier = Modifier.height(itemBottomSpace))
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
}

@Composable
private fun ProductGridMerchantPortraitItem(
    product: Product,
    quantity: Double,
    isIntegerQuantityMode: Boolean,
    parameter: Parameter,
    onRemove: () -> Unit,
    onAdd: () -> Unit
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
            if (parameter.showimageonproduct) {
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
            }

            Text(
                text = product.libelle,
                minLines = if (parameter.showimageonproduct) 1 else 2,
                maxLines =  if (parameter.showimageonproduct) 1 else 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${product.prixttc} ${parameter.devise}",
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
                    text = if (isIntegerQuantityMode) quantity.toInt().toString() else quantity.toString(),
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

@Composable
private fun ProductGridServicePortraitItem(
    product: Product,
    quantity: Int,
    parameter: Parameter,
    onRemove: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier.padding(top = 16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (parameter.showimageonproduct) {
                Row(
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    ProductImage(url = product.image?.let { Constants.UPLOAD_URL.plus(it) })
                    if (quantity > 0) {
                        Icon(
                            imageVector = CheckCircle,
                            contentDescription = "Selected",
                            tint = Primary
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f),
            ) {
                Text(
                    text = product.libelle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.padding(end = 4.dp),
                        text = "${product.prixttc} ${parameter.devise}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.End
                    ) {
                        Row(
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
                    }
                }
                product.qtestock?.let {
                    if (quantity > it) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
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
    }
}
