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
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Invoicing

@Composable
fun InvoicingInfoScreen(
    isLoading: Boolean = false,
    onSubmitForm: (Invoicing) -> Unit
) {

    var invoicing by remember { mutableStateOf(Invoicing()) }
    var isFullNameError by remember { mutableStateOf(false) }
    var isAddressError by remember { mutableStateOf(false) }

    val submitAction = {
        isFullNameError = invoicing.fullName.isEmpty()
        isAddressError = invoicing.address.isEmpty()
        if (!isFullNameError && !isAddressError) {
            // Submit the form
            onSubmitForm(invoicing)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = invoicing.fullName,
            onChange = { data ->
                invoicing = invoicing.copy(fullName = data)
                if (isFullNameError) isFullNameError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.full_name),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.full_name)
            ),
            isError = isFullNameError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = invoicing.address,
            onChange = { data ->
                invoicing = invoicing.copy(address = data)
                if (isAddressError) isAddressError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.address),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.address)
            ),
            isError = isAddressError,
            fieldColor = White,
            keyboardAction = KeyboardAction.Done,
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