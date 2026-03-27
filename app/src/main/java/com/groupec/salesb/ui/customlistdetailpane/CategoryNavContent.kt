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
import com.groupec.feature.categorydetail.CategoryDetailScreen
import com.groupec.feature.categorylist.CategoryListScreen
import com.groupec.salesb.core.model.data.Category
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    category: Category? = null,
    onNavigateToDetail: ((Category) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit,
) {
    var selectedCategory by remember { mutableStateOf<Category?>(category) }
    var refreshCategoryList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshCategoryList = !refreshCategoryList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedCategoryScreen(
                snackbarHostState = snackbarHostState,
                refreshCategoryList = refreshCategoryList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { categoryItem ->
                    selectedCategory = categoryItem
                },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshCategoryList = { refreshCategoryList = !refreshCategoryList },
                onPopBack = onPopBack,
                onNavigateToSubscription = onNavigateToSubscription,
            )
        } else {
            if (onNavigateToDetail != null) {
                CategoryListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshCategoryList = refreshCategoryList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { categoryItem ->
                        onNavigateToDetail(categoryItem)
                    },
                )
            } else if (onNavigateToHome != null) {
                CategoryDetailScreen(
                    snackbarHostState = snackbarHostState,
                    category = category,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack,
                )
            }
        }
    }
}

@Composable
fun ExpandedCategoryScreen(
    snackbarHostState: SnackbarHostState,
    refreshCategoryList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Category) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshCategoryList: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit,
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            CategoryListScreen(
                snackbarHostState = snackbarHostState,
                refreshCategoryList = refreshCategoryList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { categoryItem ->
                    selectedCategory = categoryItem
                },
            )
        }
        Box(Modifier.weight(0.6f)) {
            CategoryDetailScreen(
                snackbarHostState = snackbarHostState,
                category = selectedCategory,
                refreshCategories = onRefreshCategoryList,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                isExpandedWidth = true,
                onPopBack = onPopBack,
            )
        }
    }
}
