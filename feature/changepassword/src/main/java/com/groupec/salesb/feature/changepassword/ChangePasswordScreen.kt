package com.groupec.salesb.feature.changepassword

import android.content.Context
import android.util.Patterns
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SalesBImage
import com.groupec.salesb.core.designsystem.component.TitleHeader
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons.Person
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.ChangePasswordForm
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.LoginForm


@Composable
fun ChangePasswordScreen(
    userId: Int,
    firstLogin: Boolean,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),

    ) {
    val context = LocalContext.current
    val loginState by viewModel.changePasswordUiState.collectAsState()

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ){
        Column(modifier = Modifier.padding(24.dp),) {
            when (loginState) {
                is ChangePasswordUiState.Loading -> AppLoadingScreen(modifier = Modifier.padding(bottom = 16.dp))
                is ChangePasswordUiState.Success -> {
                    LaunchedEffect(Unit) {
                        navigateToHome()
                    }
                }
                is ChangePasswordUiState.Error -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(
                            context,
                            (loginState as ChangePasswordUiState.Error).message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

            TitleMedium(title = stringResource(id = R.string.title_first_connexion))

            Spacer(modifier = Modifier.padding(vertical = 22.dp))

            ChangePasswordForm(firstLogin = firstLogin, onSubmitForm = { passwords ->
                viewModel.changePassword(passwords)
            })
        }
    }
}

