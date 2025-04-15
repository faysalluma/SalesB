package com.groupec.salesb.feature.changepassword

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.TitleHeader
import com.groupec.salesb.core.ui.ChangePasswordForm


@Composable
fun ChangePasswordScreen(
    userId: Int,
    firstLoginOrResetPwd: Boolean,
    navigateToHome: () -> Unit,
    onBackPressed: () -> Unit,
    navigateToStartDestination: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChangePasswordViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val loginState by viewModel.changePasswordUiState.collectAsState()
    val isLoading =
        loginState is ChangePasswordUiState.Loading // Get the loading state to show circular progress in button

    BackHandler {
        if (firstLoginOrResetPwd) {
            viewModel.logout()
        }
        onBackPressed()
    }

    when (loginState) {
        is ChangePasswordUiState.Success -> {
            LaunchedEffect(Unit) {
                if (!firstLoginOrResetPwd) {
                    viewModel.logout()
                    navigateToStartDestination()
                } else {
                    navigateToHome()
                }
            }
        }

        is ChangePasswordUiState.Error -> {
            LaunchedEffect(Unit) {
                Toast.makeText(
                    context,
                    (loginState as ChangePasswordUiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetFlow()
            }
        }

        else -> {}
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize(),
    ) {
        Column(modifier = Modifier.fillMaxWidth(0.5f)) {

            TitleHeader(
                title =
                if (firstLoginOrResetPwd) stringResource(id = R.string.update_password_required)
                else stringResource(id = R.string.title_update_password),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(vertical = 16.dp))

            ChangePasswordForm(
                firstLogin = firstLoginOrResetPwd,
                onSubmitForm = { passwords ->
                    viewModel.changePassword(userId, passwords.ancPassword, passwords.password)
                },
                isLoading = isLoading
            )
        }
    }
}

