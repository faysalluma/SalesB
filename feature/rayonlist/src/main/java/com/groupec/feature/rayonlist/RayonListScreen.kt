package com.groupec.feature.rayonlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.groupec.salesb.core.UIState
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.ui.RayonCardList

@Composable
fun RayonListScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: RayonListViewModel = hiltViewModel(),
    refreshList: Boolean,
    removeSelectedBgColor: Boolean,
    onViewDetail: (Rayon) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val searchQuery by viewModel.searchQuery.collectAsState()
    val rayonState by viewModel.rayonUiState.collectAsStateWithLifecycle()
    val deleteRayonState by viewModel.deleteRayonUiState.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var rayonIdLibelle by remember { mutableStateOf(Pair(0, "")) }

    // Refresh list after insert, update or delete category
    LaunchedEffect(refreshList) {
        viewModel.getRayons() // Refresh rayon list
    }

    when (deleteRayonState) {
        is UIState.Success -> {
            LaunchedEffect(Unit) {
                focusManager.clearFocus()
                snackbarHostState.currentSnackbarData?.dismiss()
                viewModel.getRayons() // Refresh rayon list
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                    )
                )
            }
        }

        is UIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(deleteRayonState as UIState.Error).message,
                        isError = true
                    )
                )
            }
        }
        else -> {}
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        AppHeadLine(
            text = stringResource(R.string.head_title_section)
        )

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

        // List of sections
        Box {
            when (rayonState) {
                is RayonUiState.Loading -> AppLoadingScreen(text = stringResource(R.string.loading_sections))
                is RayonUiState.Empty -> EmptyScreen(stringResource(R.string.no_section_avalaible))
                is RayonUiState.Success -> {
                    RayonCardList(
                        rayons = (rayonState as RayonUiState.Success).rayons,
                        onViewDetail = onViewDetail,
                        onDelete = { id, libelle ->
                            showDialog = true
                            rayonIdLibelle = Pair(id, libelle)
                        },
                        removeSelectedBgColor = removeSelectedBgColor
                    )
                }
                is RayonUiState.Error -> ErrorScreen((rayonState as RayonUiState.Error).message)
            }
        }

        if (showDialog) {
            AppAlertInfoDialog(
                setShowDialog = {
                    showDialog = it
                    focusManager.clearFocus()
                },
                title = stringResource(com.groupec.salesb.core.ui.R.string.confirm_delete_message, rayonIdLibelle.second),
                onConfirmButton = {
                    viewModel.deleteRayon(rayonIdLibelle.first)
                },
                onDismissButton = {
                    focusManager.clearFocus()
                }
            )
        }
    }

}
