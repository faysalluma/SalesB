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
import com.groupec.feature.outputdetail.OutputDetailScreen
import com.groupec.feature.outputlist.OutputListScreen
import com.groupec.salesb.core.model.data.Output
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutputNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    output: Output? = null,
    onNavigateToDetail: ((Output) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedOutput by remember { mutableStateOf<Output?>(output) }
    var refreshList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) } // For SwipeToRefresh

    LaunchedEffect(isRefreshing) {
        // Show refresh indicator during 1s
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshList = !refreshList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedOutputScreen(
                snackbarHostState = snackbarHostState,
                refreshList = refreshList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { outputItem ->
                    selectedOutput = outputItem
                },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshList = { refreshList = !refreshList },
                onNavigateToSubscription = onNavigateToSubscription,
                onPopBack = onPopBack
            )
        } else {
            if (onNavigateToDetail != null) {
                OutputListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshList = refreshList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { outputItem ->
                        onNavigateToDetail(outputItem)
                    }
                )
            } else if (onNavigateToHome != null) {
                OutputDetailScreen(
                    snackbarHostState = snackbarHostState,
                    output = output,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack
                )
            }
        }
    }
}

@Composable
fun ExpandedOutputScreen(
    snackbarHostState: SnackbarHostState,
    refreshList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Output) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshList: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedOutput by remember { mutableStateOf<Output?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            OutputListScreen(
                snackbarHostState = snackbarHostState,
                refreshList = refreshList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { outputItem ->
                    selectedOutput = outputItem
                }
            )
        }
        Box(Modifier.weight(0.6f)) {
            OutputDetailScreen(
                snackbarHostState = snackbarHostState,
                output = selectedOutput,
                refreshList = onRefreshList,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                isExpandedWidth = true,
                onPopBack = onPopBack
            )
        }
    }
}
