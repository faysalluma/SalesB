package com.groupec.feature.outputdetail

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
import com.groupec.salesb.core.model.data.Output
import com.groupec.salesb.core.ui.OutputDataForm
import com.groupec.salesb.core.ui.OutputForm

@Composable
fun OutputDetailScreen(
    snackbarHostState: SnackbarHostState,
    output: Output?,
    refreshList: (() -> Unit)? = null,
    removeSelectedBgColor: (() -> Unit)? = null,
    isExpandedWidth: Boolean,
    navigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: OutPutDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addOutputState by viewModel.addOutputUiState.collectAsState()
    val isLoading = addOutputState is FormUIState.Loading

    var outputDataForm by remember { mutableStateOf(OutputDataForm()) }

    LaunchedEffect(output) {
        // Form Data and methods
        output?.let {
            outputDataForm = OutputDataForm(
                id = it.id.toString(),
                description = it.description,
                prix = it.prix.toString()
            )
           // Reset focus on form
           focusManager.clearFocus()
        }
    }

    val resetOutputForm = {
        outputDataForm = OutputDataForm()
        // Reset focus on form
        focusManager.clearFocus()
    }

    BackHandler {
        onPopBack?.invoke()
    }

    when (addOutputState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetOutputForm()
                if (isExpandedWidth) {
                    removeSelectedBgColor?.invoke()
                    refreshList?.invoke() // Notify list to refresh
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithState(
                            message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                        )
                    )
                } else {
                    navigateToHome?.invoke()
                }
            }
        }

        is FormUIState.Error -> {
            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message =(addOutputState as FormUIState.Error).message,
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
            text = stringResource(R.string.detail_title_output),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetOutputForm()
                        removeSelectedBgColor?.invoke()
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
            OutputForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                outputs = outputDataForm,
                onOutputDataChanged = { newOutput ->
                    outputDataForm = newOutput
                },
                onSubmitForm = { output ->
                    viewModel.addOutput(output)
                }
            )
        }
    }
}
