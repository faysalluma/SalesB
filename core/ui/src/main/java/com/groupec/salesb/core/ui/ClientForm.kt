package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun ClientForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    clients: ClientDataForm,
    onClientDataChanged: (ClientDataForm) -> Unit,
    onSubmitForm: (client: ClientDataForm) -> Unit
) {
    var isNameError by remember { mutableStateOf(false) }

    val submitAction = {
        isNameError = clients.nomprenom.isEmpty()
        if (!isNameError) {
            onSubmitForm(clients)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = clients.nomprenom,
            onChange = { data ->
                onClientDataChanged(clients.copy(nomprenom = data))
                if (isNameError) isNameError = false
            },
            label = stringResource(id = R.string.label_full_name),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_full_name)
            ),
            isError = isNameError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = clients.adresse,
            onChange = { data ->
                onClientDataChanged(clients.copy(adresse = data))
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
            value = clients.telephone,
            onChange = { data ->
                onClientDataChanged(clients.copy(telephone = data))
            },
            label = stringResource(id = R.string.label_tel),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_tel)
            ),
            fieldType = FieldType.Text,
            fieldColor = White,
            submitAction = submitAction,
            modifier = Modifier.fillMaxWidth()
        )

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

data class ClientDataForm(
    val id: String = "",
    val nomprenom: String = "",
    val adresse: String = "",
    val telephone: String = ""
)
