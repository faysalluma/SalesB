package com.groupec.feature.categorydetail

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
import com.groupec.salesb.core.model.data.Category
import com.groupec.salesb.core.ui.CategoryDataForm
import com.groupec.salesb.core.ui.CategoryForm

@Composable
fun CategoryDetailScreen(
    snackbarHostState: SnackbarHostState,
    category: Category?,
    refreshCategories: (() -> Unit)? = null,
    removeSelectedBgColor: (() -> Unit)? = null,
    isExpandedWidth: Boolean,
    navigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    viewModel: CategoryDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addCategoryState by viewModel.addCategoryUiState.collectAsState()
    val isLoading = addCategoryState is FormUIState.Loading

    var categoryDataForm by remember { mutableStateOf(CategoryDataForm()) }

    LaunchedEffect(category) {
        // Form Data and methods
       category?.let {
            categoryDataForm = CategoryDataForm(
                id = it.id.toString(),
                libelle = it.libelle,
                description = it.description.orEmpty()
            )
           // Reset focus on form
           focusManager.clearFocus()
        }
    }

    val resetCategoryForm = {
        categoryDataForm = CategoryDataForm()
        // Reset focus on form
        focusManager.clearFocus()
    }

    BackHandler {
        onPopBack?.invoke()
    }

    when (addCategoryState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetCategoryForm()
                if (isExpandedWidth) {
                    removeSelectedBgColor?.invoke()
                    refreshCategories?.invoke() // Notify list to refresh
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
                        message =(addCategoryState as FormUIState.Error).message,
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
            text = stringResource(R.string.detail_title_category),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetCategoryForm()
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
            CategoryForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                categories = categoryDataForm,
                onCategoryDataChanged = { newCategory ->
                    categoryDataForm = newCategory
                },
                onSubmitForm = { category ->
                    viewModel.addCategory(category)
                }
            )
        }
    }
}
