package com.groupec.feature.login

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SalesBImage
import com.groupec.salesb.core.designsystem.icon.AppIcons.Person
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.ui.ComposableLifecycle


@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),

) {
    ComposableLifecycle(
        // onResume = { getOrders() }
    )

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        SalesBImage(Modifier.weight(1f))
        LoginForm(Modifier.weight(1f))
    }
}

@Composable
internal fun LoginForm(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ){
        var credentials by remember { mutableStateOf(Credentials()) }

        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(26.dp)
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
                onChange = { data -> credentials = credentials.copy(email = data) },
                label = stringResource(id = R.string.label_email),
                placeholder = stringResource(id = R.string.enter_your_email) ,
                fieldType = FieldType.Email,
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
                onChange = { data -> credentials = credentials.copy(password = data) },
                label = stringResource(id = R.string.label_password),
                placeholder = stringResource(id = R.string.enter_your_password) ,
                fieldType = FieldType.Password,
                modifier = Modifier.fillMaxWidth()
            )
            Column (modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                DefaultButton(
                    onClick = { /*TODO*/ },
                    text = stringResource(id = R.string.btn_login),
                    enabled = credentials.isNotEmpty()
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = { /*TODO*/ }) {
                    Text(text = stringResource(id = R.string.forgot_password))
                }
            }
        }
    }
}

fun checkCredentials(credentials: Credentials, context: Context): Boolean {
    if (credentials.isNotEmpty()) {
        return true
    } else {
        Toast.makeText(context, "Wrong Credentials", Toast.LENGTH_SHORT).show()
        return false
    }
}


data class Credentials(
    var email: String = "",
    var password: String = "",
    var remember: Boolean = false
) {
    fun isNotEmpty(): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }
}

