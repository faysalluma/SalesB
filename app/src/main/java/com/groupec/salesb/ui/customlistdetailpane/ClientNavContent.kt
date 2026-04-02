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
import com.groupec.feature.clientdetail.ClientDetailScreen
import com.groupec.feature.clientlist.ClientListScreen
import com.groupec.salesb.core.model.data.Client
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    client: Client? = null,
    onNavigateToDetail: ((Client) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedClient by remember { mutableStateOf<Client?>(client) }
    var refreshClientList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshClientList = !refreshClientList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedClientScreen(
                snackbarHostState = snackbarHostState,
                refreshClientList = refreshClientList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { clientItem ->
                    selectedClient = clientItem
                },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshClients = { refreshClientList = !refreshClientList },
                onPopBack = onPopBack,
                onNavigateToSubscription = onNavigateToSubscription
            )
        } else {
            if (onNavigateToDetail != null) {
                ClientListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshClientList = refreshClientList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { clientItem ->
                        onNavigateToDetail(clientItem)
                    }
                )
            } else if (onNavigateToHome != null) {
                ClientDetailScreen(
                    snackbarHostState = snackbarHostState,
                    client = client,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack
                )
            }
        }
    }
}

@Composable
fun ExpandedClientScreen(
    snackbarHostState: SnackbarHostState,
    refreshClientList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Client) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshClients: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedClient by remember { mutableStateOf<Client?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            ClientListScreen(
                snackbarHostState = snackbarHostState,
                refreshClientList = refreshClientList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { clientItem ->
                    selectedClient = clientItem
                    onViewDetail(clientItem)
                }
            )
        }
        Box(Modifier.weight(0.6f)) {
            ClientDetailScreen(
                snackbarHostState = snackbarHostState,
                client = selectedClient,
                refreshClients = onRefreshClients,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                isExpandedWidth = true,
                onPopBack = onPopBack
            )
        }
    }
}
