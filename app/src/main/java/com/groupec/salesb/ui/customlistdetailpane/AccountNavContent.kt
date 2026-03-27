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
import com.groupec.accountlist.AccountListScreen
import com.groupec.feature.accountdetail.AccountDetailScreen
import com.groupec.salesb.core.model.data.User
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountNavContent(
    isExpandedWidth: Boolean,
    snackbarHostState: SnackbarHostState,
    fromDetail: Boolean = false,
    account: User? = null,
    onNavigateToDetail: ((User) -> Unit)? = null,
    onNavigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedAccount by remember { mutableStateOf<User?>(account) }
    var refreshAccountList by remember { mutableStateOf(false) }
    var removeSelectedBgColor by remember { mutableStateOf(false) }
    var isRefreshing by remember { mutableStateOf(false) } // For SwipeToRefresh

    LaunchedEffect(isRefreshing) {
        if (isRefreshing) {
            delay(1000)
            isRefreshing = false
        }
    }

    PullToRefreshBox(isRefreshing = isRefreshing, onRefresh = {
        refreshAccountList = !refreshAccountList
        isRefreshing = true
    }) {
        if (isExpandedWidth) {
            ExpandedAccountScreen(
                snackbarHostState = snackbarHostState,
                refreshAccountList = refreshAccountList,
                removeSelectedBgColor = removeSelectedBgColor,
                onViewDetail = { user -> selectedAccount = user },
                onRemoveSelectedBgColor = { removeSelectedBgColor = !removeSelectedBgColor },
                onRefreshAccounts = { refreshAccountList = !refreshAccountList },
                onNavigateToSubscription = onNavigateToSubscription,
                onPopBack = onPopBack
            )
        } else {
            if (onNavigateToDetail != null) {
                AccountListScreen(
                    snackbarHostState = snackbarHostState,
                    refreshAccountList = refreshAccountList,
                    removeSelectedBgColor = removeSelectedBgColor,
                    fromDetail = fromDetail,
                    onNavigateToSubscription = onNavigateToSubscription,
                    onViewDetail = { user ->
                        onNavigateToDetail(user)
                    }
                )
            } else if (onNavigateToHome != null) {
                AccountDetailScreen(
                    snackbarHostState = snackbarHostState,
                    account = account,
                    isExpandedWidth = false,
                    navigateToHome = onNavigateToHome,
                    onPopBack = onPopBack
                )
            }
        }
    }
}

@Composable
fun ExpandedAccountScreen(
    snackbarHostState: SnackbarHostState,
    refreshAccountList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (User) -> Unit,
    onRemoveSelectedBgColor: () -> Unit,
    onRefreshAccounts: () -> Unit,
    onPopBack: (() -> Unit)? = null,
    onNavigateToSubscription: () -> Unit
) {
    var selectedAccount by remember { mutableStateOf<User?>(null) }
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Box(Modifier.weight(0.4f)) {
            AccountListScreen(
                snackbarHostState = snackbarHostState,
                refreshAccountList = refreshAccountList,
                removeSelectedBgColor = removeSelectedBgColor,
                onNavigateToSubscription = onNavigateToSubscription,
                onViewDetail = { user ->
                    selectedAccount = user
                }
            )
        }
        Box(Modifier.weight(0.6f)) {
            AccountDetailScreen(
                snackbarHostState = snackbarHostState,
                account = selectedAccount,
                isExpandedWidth = true,
                removeSelectedBgColor = onRemoveSelectedBgColor,
                refreshAccounts = onRefreshAccounts,
                onPopBack = onPopBack
            )
        }
    }
}

