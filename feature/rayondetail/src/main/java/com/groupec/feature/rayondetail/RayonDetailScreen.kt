package com.groupec.feature.rayondetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Rayon
import com.groupec.salesb.core.ui.RayonDataForm
import com.groupec.salesb.core.ui.RayonForm

@Composable
fun RayonDetailScreen(
    snackbarHostState: SnackbarHostState,
    rayon: Rayon?,
    refreshRayons: () -> Unit,
    removeSelectedBgColor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RayonDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addRayonState by viewModel.addRayonUiState.collectAsState()
    val isLoading = addRayonState is FormUIState.Loading

    var rayonDataForm by remember { mutableStateOf(RayonDataForm()) }

    LaunchedEffect(rayon) {
        // Form Data and methods
        rayon?.let {
           rayonDataForm = RayonDataForm(
                id = it.id.toString(),
                libelle = it.libelle,
                description = it.description.orEmpty()
            )
           // Reset focus on form
           focusManager.clearFocus()
        }
    }

    val resetRayonForm = {
        rayonDataForm = RayonDataForm()
        // Reset focus on form
        focusManager.clearFocus()
    }

    when (addRayonState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetRayonForm()
                removeSelectedBgColor()
                refreshRayons() // Notify list to refresh
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                    )
                )
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(addRayonState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }

        else -> {}
    }

    Column {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 28.dp),
            text = stringResource(R.string.detail_title_rayon),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetRayonForm()
                        removeSelectedBgColor()
                    }
                )

            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize().padding(top = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RayonForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                rayons = rayonDataForm,
                onRayonDataChanged = { newRayon ->
                    rayonDataForm = newRayon
                },
                onSubmitForm = { rayon ->
                    viewModel.addRayon(rayon)
                }
            )
        }
    }
}