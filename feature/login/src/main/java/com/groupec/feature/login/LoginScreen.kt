package com.groupec.feature.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.SalesBImage
import com.groupec.salesb.core.designsystem.component.TitleHeader
import com.groupec.salesb.core.model.data.User
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.LoginForm


@Composable
fun LoginScreen(
    raisonSociale: String,
    navigateToChangePassword: (Int, Boolean) -> Unit,
    navigateToHome: (User) -> Unit,
    navigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = hiltViewModel(),

    ) {
    val context = LocalContext.current
    val loginState by viewModel.loginUiState.collectAsState()
    val isLoading =
        loginState is LoginUiState.Loading // Get the loading state to show circular progress in button

    ComposableLifecycle(
        onStop = {
            viewModel.resetFlow()
        }
    )

    Row(
        modifier = modifier.fillMaxSize()
    ) {
        SalesBImage(Modifier.weight(1f))
        Column(Modifier.weight(1f)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = modifier.fillMaxSize(),
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    when (loginState) {
                        is LoginUiState.Success -> {
                            val userInfo = (loginState as LoginUiState.Success).userInfo
                            val (user, isMainPassword) = userInfo
                            LaunchedEffect(Unit) {
                                if (user.firstlogin || (user.reset_password != null && !isMainPassword)) {
                                    navigateToChangePassword(user.id ?: 0, true)
                                } else {
                                    navigateToHome(user)
                                }
                            }
                        }

                        is LoginUiState.Error -> {
                            LaunchedEffect(Unit) {
                                Toast.makeText(
                                    context,
                                    (loginState as LoginUiState.Error).message,
                                    Toast.LENGTH_LONG
                                ).show()
                                viewModel.resetFlow() // Because if have same messages error flow dont refresh and snackBar show same things
                            }
                        }

                        else -> {}
                    }

                    TitleHeader(
                        title = stringResource(id = com.groupec.salesb.core.designsystem.R.string.title_login, raisonSociale),
                        detail = stringResource(id = com.groupec.salesb.core.designsystem.R.string.detail_login)
                    )

                    Spacer(modifier = Modifier.padding(vertical = 16.dp))

                    LoginForm(
                        onSubmitForm = { credentials ->
                            viewModel.login(credentials)
                        },
                        isLoading = isLoading,
                        onForgotPassword = navigateToForgotPassword
                    )
                }
            }
        }

    }
}

