package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.icon.AppIcons.Person
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun ForgotPasswordForm(
    onSubmitForm: (email: String) -> Unit,
    isLoading: Boolean = false
) {
    var email by remember { mutableStateOf("") }
    var isEmailError by remember { mutableStateOf(false) }

    val submitAction = {
        isEmailError = email.isEmpty() || !isValidEmail(email)
        if (!isEmailError) {
            // Submit the form
            onSubmitForm(email)
        }
    }

    Column(
        modifier = Modifier.wrapContentWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = email,
            leadingIcon = {
                Icon(
                    Person,
                    contentDescription = null,
                    tint = Primary
                )
            },
            onChange = { data ->
                email = data
                if (isEmailError) isEmailError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_email),
            placeholder = stringResource(id = R.string.enter_your_email),
            fieldType = FieldType.Email,
            isError = isEmailError,
            supportingText = if (email.isNotEmpty() && !isValidEmail(email)) {
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
            keyboardAction = KeyboardAction.Done,
            submitAction = submitAction,
            modifier = Modifier.fillMaxWidth()
        )

        DefaultButton(
            onClick = submitAction,
            text = stringResource(id = R.string.btn_reset_password),
            isLoading = isLoading
        )
    }
}