package com.groupec.feature.clientdetail

import androidx.activity.compose.BackHandler
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
import com.groupec.salesb.core.model.data.Client
import com.groupec.salesb.core.ui.ClientDataForm
import com.groupec.salesb.core.ui.ClientForm

@Composable
fun ClientDetailScreen(
    snackbarHostState: SnackbarHostState,
    client: Client?,
    refreshClients: (() -> Unit)? = null,
    removeSelectedBgColor: (() -> Unit)? = null,
    isExpandedWidth: Boolean,
    navigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: ClientDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addClientState by viewModel.addClientUiState.collectAsState()
    val isLoading = addClientState is FormUIState.Loading

    var clientDataForm by remember { mutableStateOf(ClientDataForm()) }

    LaunchedEffect(client) {
        client?.let {
            clientDataForm = ClientDataForm(
                id = it.id.toString(),
                nomprenom = it.nomprenom,
                adresse = it.adresse.orEmpty(),
                telephone = it.telephone.orEmpty()
            )
            focusManager.clearFocus()
        }
    }

    val resetClientForm = {
        clientDataForm = ClientDataForm()
        focusManager.clearFocus()
    }

    BackHandler {
        onPopBack?.invoke()
    }

    when (addClientState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetClientForm()
                if (isExpandedWidth) {
                    removeSelectedBgColor?.invoke()
                    refreshClients?.invoke()
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithState(
                            message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                        )
                    )
                    viewModel.resetFlow()
                } else {
                    navigateToHome?.invoke()
                }
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = (addClientState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }

        else -> {}
    }

    Column(modifier = modifier) {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 28.dp),
            text = stringResource(R.string.detail_title_client),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetClientForm()
                        removeSelectedBgColor?.invoke()
                    }
                )
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ClientForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                clients = clientDataForm,
                onClientDataChanged = { newClient ->
                    clientDataForm = newClient
                },
                onSubmitForm = { form ->
                    viewModel.addClient(form)
                }
            )
        }
    }
}
