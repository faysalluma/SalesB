package com.groupec.feature.outputlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.ui.OutputCardList

@Composable
fun OutputListScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: OutputListViewModel = hiltViewModel(),
    refreshList: Boolean,
    removeSelectedBgColor: Boolean,
    fromDetail: Boolean = false,
    onViewDetail: (Output) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val outputs = viewModel.pagedOutputs.collectAsLazyPagingItems()
    val error = (outputs.loadState.refresh as? LoadState.Error)?.error?.message
    val deleteOutputState by viewModel.deleteOutputUiState.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var outputIdLibelle by remember { mutableStateOf(Pair(0, "")) }

    // Refresh list after insert, update or delete output
    LaunchedEffect(refreshList) {
        outputs.refresh() // Refresh the LazyPagingItems
    }

    // Refresh data from detail when we are on portrait/medium Mode
    LaunchedEffect(Unit) {
        if (fromDetail) {
            snackbarHostState.showSnackbar(
                SnackbarVisualsWithState(
                    message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                )
            )
        }
    }

    when (deleteOutputState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                focusManager.clearFocus()
                snackbarHostState.currentSnackbarData?.dismiss()
                outputs.refresh() // Refresh the LazyPagingItems
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                    )
                )
                viewModel.resetFlow()
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(deleteOutputState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }
        else -> {}
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AppHeadLine(
            text = stringResource(R.string.head_title_output)
        )

        // if get error when fetching outputs
        if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorScreen(
                    error = error,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 16.dp)
                )
                DefaultButton(
                    modifier = Modifier.wrapContentWidth(),
                    text = stringResource(com.groupec.salesb.core.ui.R.string.retry)
                ) {
                    outputs.refresh()
                }
            }
        } else {
            // Show Progress bar waiting load outputs
            if (!isSearching && outputs.itemCount == 0) {
                AppLoadingScreen(text = stringResource(R.string.loading_outputs))
            } else {
                // Barre de recherche
                AppTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 16.dp),
                    value = searchQuery,
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.Search,
                            contentDescription = "Search icon"
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                Icon(
                                    imageVector = AppIcons.Close,
                                    contentDescription = "Clear text"
                                )
                            }
                        }
                    },
                    onChange = { viewModel.updateSearchQuery(it) },
                    placeholder = stringResource(com.groupec.salesb.core.ui.R.string.search_place_holder),
                    fieldType = FieldType.Text,
                    fieldColor = Silver,
                    shape = RoundedCornerShape(28.dp)
                )

                // Liste paginée
                if (outputs.itemCount == 0) {
                    EmptyScreen()
                } else {
                    OutputCardList(
                        outputs = outputs,
                        isSearching = isSearching,
                        onViewDetail = onViewDetail,
                        onDelete = { id, libelle ->
                            showDialog = true
                            outputIdLibelle = Pair(id, libelle)
                        },
                        removeSelectedBgColor = removeSelectedBgColor
                    )
                }

                if (showDialog) {
                    AppAlertInfoDialog(
                        setShowDialog = {
                            showDialog = it
                            focusManager.clearFocus()
                        },
                        title = stringResource(com.groupec.salesb.core.ui.R.string.confirm_delete_message, outputIdLibelle.second),
                        onConfirmButton = {
                            viewModel.deleteOutput(outputIdLibelle.first)
                        },
                        onDismissButton = {
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        }
    }

}
