package com.groupec.feature.accountdetail

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
import androidx.compose.runtime.mutableStateMapOf
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
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.model.data.toStringList
import com.groupec.salesb.core.ui.UserDataForm
import com.groupec.salesb.core.ui.UserForm

@Composable
fun AccountDetailScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    account: User?,
    refreshAccounts: (() -> Unit) ? = null,
    removeSelectedBgColor: (() -> Unit) ? = null,
    navigateToHome: (() -> Unit) ? = null,
    onPopBack: (() -> Unit) ? = null,
    isExpandedWidth: Boolean,
    viewModel: AccountDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addUserState by viewModel.addUserUiState.collectAsState()
    val isLoading = addUserState is FormUIState.Loading
    var userDataForm by remember { mutableStateOf(UserDataForm()) }
    val privilegesState = remember { mutableStateMapOf<String, MutableSet<String>>() }
    val firstActifValue = stringResource(com.groupec.salesb.core.ui.R.string.active)
    var actifState by remember { mutableStateOf(firstActifValue) }
    val activeList = listOf(
        stringResource(com.groupec.salesb.core.ui.R.string.active),
        stringResource(com.groupec.salesb.core.ui.R.string.disable)
    )

    // Reset privileges state list
    val resetPrivilegesStateList = {
        // Initialiser toutes les entrées avec Set vide si nécessaire
        Privileges.entries.forEach { privilege ->
            privilegesState[privilege.name] = mutableSetOf()
        }
        actifState = activeList[0]
        userDataForm = userDataForm.copy(actif=1, privilege = emptyList())
    }

    // When on detail and popBackStack : avoid show toast message
    BackHandler {
        onPopBack?.invoke()
    }

    LaunchedEffect(account) {
        // Form Data and methods
        account?.let { it ->
            userDataForm = UserDataForm(
                id = it.id.toString(),
                nomprenom = it.nomprenom,
                adresse = it.adresse.orEmpty(),
                tel = it.tel.orEmpty(),
                email = it.email,
                password = "",
                actif = if (it.actif) 1 else 0,
                privilege = it.privilege?.toStringList() ?: emptyList()
            )
            actifState = if (it.actif) activeList[0] else activeList[1]

            // List to send to form
            privilegesState.clear()
            it.privilege?.toStringList()?.forEach { key ->
                Privileges.entries.find { privilege ->
                    privilege.values.containsKey(key)
                }?.let { privilege ->
                    val currentKeys = privilegesState.getOrPut(privilege.name) { mutableSetOf() }
                    currentKeys.add(key)
                    privilegesState[privilege.name] = currentKeys
                }
            } ?: resetPrivilegesStateList()
           // Reset focus on form
           focusManager.clearFocus()
        }
    }

    val resetUserForm = {
        userDataForm = UserDataForm()
        resetPrivilegesStateList()
        // Reset focus on form
        focusManager.clearFocus()
    }

    when (addUserState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetUserForm()
                if (isExpandedWidth) {
                    removeSelectedBgColor?.invoke()
                    refreshAccounts?.invoke() // Notify list to refresh
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
                        message =(addUserState as FormUIState.Error).message,
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
            text = stringResource(R.string.detail_title_user),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetUserForm()
                        removeSelectedBgColor?.invoke()
                    }
                )

            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val activeList = listOf(
                stringResource(com.groupec.salesb.core.ui.R.string.active),
                stringResource(com.groupec.salesb.core.ui.R.string.disable)
            )

            UserForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                users = userDataForm,
                actifItems = activeList,
                actifState = actifState,
                onActifState = { newActif ->
                    actifState = newActif
                },
                privilegesState = privilegesState,
                onUserDataChanged = { newUser ->
                    userDataForm = newUser
                },
                onSubmitForm = { user ->
                    viewModel.addUser(user)
                }
            )
        }
    }
}