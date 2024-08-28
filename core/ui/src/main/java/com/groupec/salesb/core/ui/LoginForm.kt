package com.groupec.salesb.core.ui

import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.groupec.salesb.core.designsystem.icon.AppIcons.Person
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun LoginForm(onSubmitForm: () -> Unit) {

    var credentials by remember { mutableStateOf(Credentials()) }
    var isEmailError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = credentials.email,
            leadingIcon = {
                Icon(
                    Person,
                    contentDescription = null,
                    tint = Primary
                )
            },
            onChange = { data ->
                credentials = credentials.copy(email = data)
                if (isEmailError) isEmailError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_email),
            placeholder = stringResource(id = R.string.enter_your_email) ,
            fieldType = FieldType.Email,
            isError = isEmailError,
            modifier = Modifier.fillMaxWidth()
        )
        AppTextField(
            value = credentials.password,
            leadingIcon = {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.key),
                    contentDescription = null,
                    tint = Primary
                )
            },
            onChange = { data ->
                credentials = credentials.copy(password = data)
                if (isPasswordError) isPasswordError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_password),
            placeholder = stringResource(id = R.string.enter_your_password) ,
            fieldType = FieldType.Password,
            isError = isPasswordError,
            modifier = Modifier.fillMaxWidth()
        )
        Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            DefaultButton(
                onClick = {
                    isEmailError = credentials.email.isEmpty() || !isValidEmail(credentials.email)
                    isPasswordError = credentials.password.isEmpty()
                    if (!isEmailError && !isPasswordError) {
                        // Submit the form
                        onSubmitForm()
                    }
                },
                text = stringResource(id = R.string.btn_login),
                // enabled = credentials.isNotEmpty()
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = stringResource(id = R.string.forgot_password))
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

data class Credentials(
    var email: String = "",
    var password: String = ""
)