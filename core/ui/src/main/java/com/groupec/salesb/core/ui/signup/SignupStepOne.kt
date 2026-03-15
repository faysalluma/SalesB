package com.groupec.salesb.core.ui.signup

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.ui.R

@Composable
fun SignupStepOne(
    state: SignupStepOneFormState,
    showErrors: Boolean,
    emailErrorMessage: String?,
    onValueChange: (SignupStepOneFormState) -> Unit
) {
    Text(
        stringResource(R.string.signup_create_account),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
    Text(
        stringResource(R.string.signup_step_1),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge, color = Color.Gray,
        modifier = Modifier.padding(bottom = 18.dp)
    )
    SignupStepOneForm(state, showErrors, emailErrorMessage, onValueChange)
}

@Composable
private fun SignupStepOneForm(
    state: SignupStepOneFormState,
    showErrors: Boolean,
    emailErrorMessage: String?,
    onValueChange: (SignupStepOneFormState) -> Unit
) {
    val isFullNameError = showErrors && state.fullName.isBlank()
    val isEmailError = showErrors &&
        (state.email.isBlank() || !isValidEmail(state.email) || !emailErrorMessage.isNullOrBlank())
    val isPasswordError = showErrors && state.password.isBlank()
    val isConfirmationError = showErrors &&
        (state.confirmPassword.isBlank() || !state.arePasswordsMatching())

    AppTextField(
        value = state.fullName,
        onChange = { data ->
            onValueChange(state.copy(fullName = data))
        },
        label = stringResource(R.string.signup_name),
        placeholder = stringResource(R.string.signup_full_name_placeholder),
        isError = isFullNameError,
        fieldColor = White,
        leadingIcon = { Icon(Icons.Default.Person, null) },
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.email,
        onChange = { data ->
            onValueChange(state.copy(email = data))
        },
        label = stringResource(R.string.signup_email),
        placeholder = stringResource(R.string.signup_email_placeholder),
        fieldType = FieldType.Email,
        leadingIcon = { Icon(Icons.Default.Email, null) },
        isError = isEmailError,
        supportingText = when {
            showErrors && state.email.isNotEmpty() && !isValidEmail(state.email) -> {
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(com.groupec.salesb.core.designsystem.R.string.invalid_email),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            showErrors && !emailErrorMessage.isNullOrBlank() -> {
                {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = emailErrorMessage,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            else -> null
        },
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.password,
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        onChange = { data ->
            onValueChange(state.copy(password = data))
        },
        label = stringResource(id = com.groupec.salesb.core.designsystem.R.string.label_password),
        placeholder = stringResource(id = com.groupec.salesb.core.designsystem.R.string.label_password),
        fieldType = FieldType.Password,
        isError = isPasswordError,
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.confirmPassword,
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        onChange = { data ->
            onValueChange(state.copy(confirmPassword = data))
        },
        label = stringResource(id = com.groupec.salesb.core.designsystem.R.string.label_confirmation),
        placeholder = stringResource(id = com.groupec.salesb.core.designsystem.R.string.label_confirmation),
        fieldType = FieldType.Password,
        isError = isConfirmationError,
        supportingText = if (showErrors && state.confirmPassword.isNotEmpty() && !state.arePasswordsMatching()){
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(com.groupec.salesb.core.designsystem.R.string.enter_password_not_matching),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            null
        },
        fieldColor = White,
        keyboardAction = KeyboardAction.Done,
        submitAction = { },
        modifier = Modifier.fillMaxWidth()
    )
}

data class SignupStepOneFormState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
) {
    fun arePasswordsMatching(): Boolean {
        return password == confirmPassword
    }
}
