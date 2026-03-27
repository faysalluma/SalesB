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
import com.groupec.feature.rayondetail.RayonDetailScreen
import com.groupec.feature.rayonlist.RayonListScreen
import com.groupec.salesb.core.model.data.Rayon
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RayonNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    rayon: Rayon? = null,
    onNavigateToDetail: ((Rayon) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit,
) {
    var selectedRayon by remember { mutableStateOf<Rayon?>(rayon) }
    var refreshList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
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
            ExpandedRayonScreen(
                snackbarHostState = snackbarHostState,
                refreshList = refreshList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { rayonItem ->
                    selectedRayon = rayonItem
                },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshList = { refreshList = !refreshList },
                onPopBack = onPopBack,
                onNavigateToSubscription = onNavigateToSubscription,
            )
        } else {
            if (onNavigateToDetail != null) {
                RayonListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshList = refreshList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { rayonItem ->
                        onNavigateToDetail(rayonItem)
                    },
                )
            } else if (onNavigateToHome != null) {
                RayonDetailScreen(
                    snackbarHostState = snackbarHostState,
                    rayon = rayon,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack,
                )
            }
        }
    }
}

@Composable
fun ExpandedRayonScreen(
    snackbarHostState: SnackbarHostState,
    refreshList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Rayon) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshList: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit,
) {
    var selectedRayon by remember { mutableStateOf<Rayon?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            RayonListScreen(
                snackbarHostState = snackbarHostState,
                refreshList = refreshList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { rayonItem ->
                    selectedRayon = rayonItem
                },
            )
        }
        Box(Modifier.weight(0.6f)) {
            RayonDetailScreen(
                snackbarHostState = snackbarHostState,
                rayon = selectedRayon,
                refreshRayons = onRefreshList,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                isExpandedWidth = true,
                onPopBack = onPopBack,
            )
        }
    }
}
