package com.groupec.salesb.core.ui

import androidx.collection.mutableIntListOf
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppCheckboxMinimal
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenu
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.AppCheckboxParent
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.TitledBox
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.theme.White
import kotlin.random.Random

@Composable
fun UserForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    users: UserDataForm,
    actifItems: List<String>,
    actifState: String,
    onActifState: (String) -> Unit,
    privilegesState: MutableMap<String, MutableSet<String>>,
    onUserDataChanged: (UserDataForm) -> Unit,
    onSubmitForm: (users: UserDataForm) -> Unit
) {

    val context = LocalContext.current
    var isNomPrenomError by remember { mutableStateOf(false) }
    var isEmailError by remember { mutableStateOf(false) }

    val submitAction = {
        isNomPrenomError = users.nomprenom.isEmpty()
        isEmailError = users.email.isEmpty() || !isValidEmail(users.email)

        if (!isNomPrenomError && !isEmailError) {
            // Submit the form
            onSubmitForm(users)
        }
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        AppTextField(
            value = users.nomprenom,
            onChange = { data ->
                onUserDataChanged(users.copy(nomprenom = data))
                if (isNomPrenomError) isNomPrenomError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_full_name),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_full_name)
            ),
            isError = isNomPrenomError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = users.adresse,
            onChange = { data ->
                onUserDataChanged(users.copy(adresse = data))
            },
            label = stringResource(id = R.string.label_address),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_address)
            ),
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = users.tel,
            onChange = { data ->
                onUserDataChanged(users.copy(tel = data))
            },
            label = stringResource(id = R.string.label_tel),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_tel)
            ),
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = users.email,
            onChange = { data ->
                onUserDataChanged(users.copy(email = data))
                if (isEmailError) isEmailError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_email_required),
            placeholder = stringResource(id = R.string.enter_your_email_required),
            fieldType = FieldType.Email,
            isError = isEmailError,
            fieldColor = White,
            supportingText = if (users.email.isNotEmpty() && !isValidEmail(users.email)) {
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.invalid_email),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                null
            },
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = users.password,
            onChange = { data ->
                onUserDataChanged(users.copy(password = data))
            },
            label = stringResource(id = R.string.label_password),
            placeholder = stringResource(id = R.string.enter_your_password),
            fieldType = FieldType.Text,
            fieldColor = White,
            submitAction = submitAction,
            modifier = Modifier.fillMaxWidth()
        )

        AppExposedDropdownMenu(
            modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
            items = actifItems,
            value = actifState,
            onValueChange = {
                onActifState(it)
            }
        ) { index, item ->
            onUserDataChanged(users.copy(actif = if (index == 1) 0 else 1))
        }

        TitledBox(
            modifier = Modifier.fillMaxWidth(),
            title = stringResource(id = R.string.privileges)
        ) {
           Privileges.entries.forEachIndexed { index, privilege ->
               val selectedChildren = privilegesState[privilege.name]?.toList() ?: emptyList()
               if (privilege.values.size == 1) {
                   AppCheckboxMinimal(
                       label = privilege.getTitle(context),
                       child = privilege.values.entries.first().key,
                       selectedChildren = selectedChildren
                   ) { approval ->

                       val keySet = approval
                           ?.let { privilege.getKeyByApproval(it) }
                           ?.let { mutableSetOf(it) }
                           ?: mutableSetOf()

                       // Mettre à jour le state pour ce privilege
                       privilegesState[privilege.name] = keySet

                       // Fusionner toutes les clés cochées pour mettre à jour
                       val allSelected = privilegesState.values.flatten().distinct()
                       onUserDataChanged(users.copy(privilege = allSelected.toMutableList()))
                   }
               } else {
                   AppCheckboxParent(
                       parentLabel = privilege.getTitle(context),
                       children = privilege.values.entries.drop(1).associate { it.toPair() },// Supprimer le premier élément du map
                       selectedChildren = selectedChildren
                   ) { checkedList ->
                       // Mettre à jour le state pour ce privilege
                       privilegesState[privilege.name] = checkedList.toMutableSet()

                       // Fusionner toutes les clés cochées pour mettre à jour
                       val allSelected = privilegesState.values.flatten().distinct()
                       onUserDataChanged(users.copy(privilege = allSelected.toMutableList()))
                   }
               }
               if (index < Privileges.entries.lastIndex) {
                   HorizontalDivider(Modifier.padding(vertical = 10.dp))
               }
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DefaultButton(
                onClick = submitAction,
                text = stringResource(id = R.string.btn_save),
                isLoading = isLoading
            )
        }
    }
}

data class UserDataForm(
    val id: String = "",
    val nomprenom: String = "",
    val adresse: String = "",
    val tel: String = "",
    val email: String = "",
    val password: String = Random.nextInt(10000, 100000).toString(),
    val actif: Int = 1,
    val privilege: List<String> = emptyList()
)