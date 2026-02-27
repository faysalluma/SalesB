package com.groupec.feature.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.feature.signup.designsystem.SignupStepIndicator
import com.groupec.feature.signup.designsystem.SignupTopBar
import com.groupec.feature.signup.designsystem.SignupBottomActions
import com.groupec.feature.signup.ui.SignupStepOneContent
import com.groupec.feature.signup.ui.SignupStepThreeContent
import com.groupec.feature.signup.ui.SignupStepTwoContent
import com.groupec.feature.signup.ui.SignupSuccessContent
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.saveable.rememberSaveable

data class SignupStepOneFormState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = ""
)

data class SignupStepTwoFormState(
    val companyName: String = "",
    val companyType: String = "",
    val email: String = "",
    val address: String = "",
    val phone: String = "",
    val ifu: String = "",
    val website: String = ""
)

@Composable
fun SignupScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = hiltViewModel()
) {
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val uiState by viewModel.signupConfigurationUiState.collectAsState()
    var showPrintServiceInfo by rememberSaveable { mutableStateOf(false) }

    var stepOne by rememberSaveable { mutableStateOf(SignupStepOneFormState()) }
    var stepTwo by rememberSaveable { mutableStateOf(SignupStepTwoFormState()) }

    var showStepOneErrors by rememberSaveable { mutableStateOf(false) }
    var showStepTwoErrors by rememberSaveable { mutableStateOf(false) }

    if (showPrintServiceInfo) {
        AppAlertInfoDialog(
            setShowDialog = { showPrintServiceInfo = it },
            title = stringResource(R.string.signup_print_service_info_title),
            message = stringResource(R.string.signup_print_service_info_message),
            confirmButtonText = stringResource(android.R.string.ok),
            onConfirmButton = {}
        )
    }

    if (uiState is SignupConfigurationUiState.Error) {
        AppAlertInfoDialog(
            setShowDialog = {
                if (!it) viewModel.consumeError()
            },
            title = stringResource(R.string.signup_configuration_error_title),
            message = (uiState as SignupConfigurationUiState.Error).message,
            confirmButtonText = stringResource(android.R.string.ok),
            onConfirmButton = { viewModel.consumeError() }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        SignupTopBar(
            title = if (currentStep < 3) stringResource(R.string.signup_title) else stringResource(R.string.signup_configurations),
            onBack = { if (currentStep > 1) currentStep -= 1 }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SignupStepIndicator(step = currentStep)

            when (currentStep) {
                1 -> SignupStepOneContent(
                    state = stepOne,
                    showErrors = showStepOneErrors,
                    onValueChange = { stepOne = it },
                    onLoginClick = navigateToLogin
                )

                2 -> SignupStepTwoContent(
                    state = stepTwo,
                    showErrors = showStepTwoErrors,
                    onValueChange = { stepTwo = it }
                )

                3 -> SignupStepThreeContent(onInfoClick = { showPrintServiceInfo = true })
                else -> SignupSuccessContent()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SignupBottomActions(
            currentStep = currentStep,
            isLoading = uiState is SignupConfigurationUiState.Loading,
            onPrevious = { if (currentStep > 1) currentStep -= 1 },
            onNext = {
                when (currentStep) {
                    1 -> {
                        val valid = stepOne.fullName.isNotBlank() &&
                            stepOne.email.isNotBlank() &&
                            stepOne.password.isNotBlank() &&
                            stepOne.confirmPassword.isNotBlank()
                        showStepOneErrors = !valid
                        if (valid) currentStep = 2
                    }

                    2 -> {
                        val valid = stepTwo.companyName.isNotBlank() && stepTwo.companyType.isNotBlank()
                        showStepTwoErrors = !valid
                        if (valid) currentStep = 3
                    }

                    else -> viewModel.saveInitialConfiguration()
                }
            },
            onStartExperience = navigateToHome
        )
    }

    LaunchedEffect(uiState, currentStep) {
        if (uiState is SignupConfigurationUiState.Success && currentStep == 3) {
            currentStep = 4
        }
    }
}
