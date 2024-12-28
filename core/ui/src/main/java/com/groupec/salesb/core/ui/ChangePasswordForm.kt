package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun ChangePasswordForm(firstLogin: Boolean, onSubmitForm: (password: Password) -> Unit, isLoading: Boolean = false) {

    var passwords by remember { mutableStateOf(Password()) }
    var isAncPasswordError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var isConfirmationError by remember { mutableStateOf(false) }

    val submitAction = {
        isAncPasswordError = !firstLogin && passwords.ancPassword.isEmpty()
        isPasswordError = passwords.password.isEmpty()
        isConfirmationError = passwords.confirmation.isEmpty() || !passwords.arePasswordsMatching()
        if (!isAncPasswordError && !isPasswordError && !isConfirmationError) {
            // Submit the form
            onSubmitForm(passwords)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        if (!firstLogin) {
            AppTextField(
                value = passwords.ancPassword,
                leadingIcon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.key),
                        contentDescription = null,
                        tint = Primary
                    )
                },
                onChange = { data ->
                    passwords = passwords.copy(ancPassword = data)
                    if (isAncPasswordError) isAncPasswordError = false //  Clear error when user starts typing
                },
                label = stringResource(id = R.string.label_anc_password),
                placeholder = stringResource(id = R.string.enter_your_anc_password),
                fieldType = FieldType.Password,
                isError = isAncPasswordError,
                modifier = Modifier.fillMaxWidth()
            )
        }
        AppTextField(
            value = passwords.password,
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.key),
                    contentDescription = null,
                    tint = Primary
                )
            },
            onChange = { data ->
                passwords = passwords.copy(password = data)
                if (isPasswordError) isPasswordError = false //  Clear error when user starts typing
            },
            label = if (firstLogin) {
                stringResource(id = R.string.label_new_password)
            } else {
                stringResource(id = R.string.label_password)
            },
            placeholder = if (firstLogin) {
                stringResource(id = R.string.enter_your_new_password)
            } else {
                stringResource(id = R.string.enter_your_password)
            },
            fieldType = FieldType.Password,
            isError = isPasswordError,
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = passwords.confirmation,
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.key),
                    contentDescription = null,
                    tint = Primary
                )
            },
            onChange = { data ->
                passwords = passwords.copy(confirmation = data)
                if (isConfirmationError) isConfirmationError =
                    false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_confirmation),
            placeholder = stringResource(id = R.string.enter_your_confirmation),
            fieldType = FieldType.Password,
            isError = isConfirmationError,
            supportingText = if (passwords.confirmation.isNotEmpty() && !passwords.arePasswordsMatching()){
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.enter_password_not_matching),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            } else {
                null
            },
            keyboardAction = KeyboardAction.Done,
            submitAction = submitAction,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DefaultButton(
                onClick = submitAction,
                text = stringResource(id = R.string.btn_change_password),
                isLoading = isLoading
            )
        }
    }
}


data class Password(
    var ancPassword: String = "",
    var password: String = "",
    var confirmation: String = ""
) {
    fun arePasswordsMatching(): Boolean {
        return password == confirmation
    }
}