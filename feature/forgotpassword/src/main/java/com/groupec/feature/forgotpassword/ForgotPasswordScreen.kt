package com.groupec.feature.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.ResultCardPreview
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.ui.ErrorCard
import com.groupec.salesb.core.ui.ForgotPasswordForm
import com.groupec.salesb.core.ui.ProductGridAdaptive
import com.groupec.salesb.core.ui.SaleDetailCard
import com.groupec.salesb.core.ui.SuccessCard

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onClose: () -> Unit
) {
    val forgotPasswordUiState by viewModel.forgotPasswordUiState.collectAsState()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        when (forgotPasswordUiState) {
            is FormUIState.Idle -> {
                ForgotPasswordFormScreen { email ->
                    viewModel.resetPassword(email)
                }
            }
            is FormUIState.Loading -> {
                AppLoadingScreen(
                    text = stringResource(com.groupec.salesb.core.designsystem.R.string.in_progress)
                )
            }
            is FormUIState.Success -> {
                SuccessCard(
                    message = stringResource(R.string.send_email_message),
                    onAction = onClose
                )
            }
            is FormUIState.Error -> {
                ErrorCard(
                    message = (forgotPasswordUiState as FormUIState.Error).message,
                    onAction = onClose
                )
            }
            else -> {}
        }
    }
}

@Composable
fun ForgotPasswordFormScreen(onSubmitForm: (email: String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.5f)
            .fillMaxHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TitleLarge(
            title = stringResource(com.groupec.salesb.core.designsystem.R.string.forgot_password),
            fontWeight = FontWeight.W500,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        TextNormal(
            text = stringResource(com.groupec.salesb.core.designsystem.R.string.password_forgot_details_info),
            modifier = Modifier.padding(bottom = 32.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
        ForgotPasswordForm(
            onSubmitForm = onSubmitForm
        )
    }
}

