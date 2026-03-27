package com.groupec.salesb.ui.customlistdetailpane

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.groupec.feature.productdetail.ProductDetailScreen
import com.groupec.feature.productlist.ProductListScreen
import com.groupec.salesb.core.model.data.Product
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    product: Product? = null,
    onNavigateToDetail: ((Product) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToCategory: () -> Unit,
    onNavigateToRayon: () -> Unit,
    onNavigateToSubscription: () -> Unit,
) {
    var selectedProduct by remember { mutableStateOf<Product?>(product) }
    var refreshProductList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) } // For SwipeToRefresh

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshProductList = !refreshProductList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedProductScreen(
                snackbarHostState = snackbarHostState,
                refreshProductList = refreshProductList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { product -> selectedProduct = product },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshProducts = { refreshProductList = !refreshProductList },
                onNavigateToCategory = onNavigateToCategory,
                onNavigateToRayon = onNavigateToRayon,
                onNavigateToSubscription = onNavigateToSubscription,
                onPopBack = onPopBack
            )
        } else {
            if (onNavigateToDetail != null) {
                ProductListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshProductList = refreshProductList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { selectedProduct ->
                        onNavigateToDetail(selectedProduct)
                    }
                )
            } else if (onNavigateToHome != null) {
                ProductDetailScreen(
                    snackbarHostState = snackbarHostState,
                    product = product,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack,
                    navigateToCategory = onNavigateToCategory,
                    navigateToRayon = onNavigateToRayon,
                    onNavigateToSubscription = onNavigateToSubscription
                )
            }
        }
    }
}



@Composable
fun ExpandedProductScreen(
    snackbarHostState: SnackbarHostState,
    refreshProductList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Product) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshProducts: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToCategory: () -> Unit,
    onNavigateToRayon: () -> Unit,
    onNavigateToSubscription: () -> Unit
) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            ProductListScreen(
                snackbarHostState = snackbarHostState,
                refreshProductList = refreshProductList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { product ->
                    selectedProduct = product
                }
            )
        }
        Box(Modifier.weight(0.6f)) {
            ProductDetailScreen(
                snackbarHostState = snackbarHostState,
                product = selectedProduct,
                isExpandedWidth = true,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                refreshProducts = onRefreshProducts,
                onPopBack = onPopBack,
                navigateToCategory = onNavigateToCategory,
                navigateToRayon = onNavigateToRayon,
                onNavigateToSubscription = onNavigateToSubscription
            )
        }
    }
}
