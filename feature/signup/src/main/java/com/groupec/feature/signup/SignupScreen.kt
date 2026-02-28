package com.groupec.feature.signup

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.SignupStepIndicator
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.ui.signup.SignupStepOne
import com.groupec.salesb.core.ui.signup.SignupStepOneFormState
import com.groupec.salesb.core.ui.signup.SignupStepThree
import com.groupec.salesb.core.ui.signup.SignupStepThreeFormState
import com.groupec.salesb.core.ui.signup.SignupStepTwo
import com.groupec.salesb.core.ui.signup.SignupStepTwoFormState
import com.groupec.salesb.core.ui.signup.SignupSuccessContent
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(
    navigateToLogin: () -> Unit,
    navigateToHome: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignupViewModel = hiltViewModel()
) {
    var currentStep by rememberSaveable { mutableStateOf(1) }
    val uiState by viewModel.signupConfigurationUiState.collectAsState()

    var stepOne by remember { mutableStateOf(SignupStepOneFormState()) }
    var stepTwo by remember { mutableStateOf(SignupStepTwoFormState()) }
    var stepThree by remember { mutableStateOf(SignupStepThreeFormState()) }

    var showStepOneErrors by remember { mutableStateOf(false) }
    var showStepTwoErrors by remember { mutableStateOf(false) }
    var showStepThreeErrors by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var uri by remember { mutableStateOf<Uri?>(null) }
    val firstActifValue = stringResource(R.string.select)
    var typeCompanyState by remember { mutableStateOf(firstActifValue) }
    val typeCompanyList = listOf(
        stringResource(R.string.select),
        stringResource(R.string.sales_and_retailers),
        stringResource(R.string.professional_services)
    )

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
        SignupStepIndicator(step = currentStep, modifier = Modifier.padding(16.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentStep) {
                1 -> SignupStepOne(
                    state = stepOne,
                    showErrors = showStepOneErrors,
                    onValueChange = { stepOne = it }
                )

                2 -> SignupStepTwo(
                    state = stepTwo,
                    showErrors = showStepTwoErrors,
                    uri = uri,
                    onUriChange = {
                        uri = it
                    },
                    deleteImageFromCache = { filename ->
                        viewModel.deleteImageFromCache(context, filename)
                    },
                    typeCompanyItems = typeCompanyList,
                    typeCompanyState = typeCompanyState,
                    ontypeCompanyState = { newType ->
                        typeCompanyState = newType
                    },
                    onValueChange = { stepTwo = it }
                )

                3 -> SignupStepThree(
                   state = stepThree,
                   showErrors = showStepThreeErrors,
                   onValueChange = { stepThree = it }
                )
                else -> SignupSuccessContent()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SignupBottomActions(
            currentStep = currentStep,
            isLoading = uiState is SignupConfigurationUiState.Loading,
            onPrevious = {
                if (currentStep > 1) {
                    currentStep -= 1
                    coroutineScope.launch {
                        scrollState.animateScrollTo(0)
                    }
                }
            },
            onNext = {
                when (currentStep) {
                    1 -> {
                        val valid = stepOne.fullName.isNotBlank() &&
                            stepOne.email.isNotBlank() &&
                            isValidEmail(stepOne.email) &&
                            stepOne.password.isNotBlank() &&
                            stepOne.confirmPassword.isNotBlank() &&
                            stepOne.arePasswordsMatching()
                        showStepOneErrors = !valid
                        if (valid) currentStep = 2
                        if (currentStep == 2) {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        }
                    }

                    2 -> {
                        val valid = stepTwo.isValid()
                        showStepTwoErrors = !valid
                        if (valid) {
                            currentStep = 3
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        }
                    }

                    else -> {
                        val valid = stepThree.isValid()
                        showStepThreeErrors = !valid
                        if (valid) {
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                            viewModel.saveInitialConfiguration()
                        }
                    }
                }
            },
            navigateToLogin = navigateToLogin,
            onStartExperience = {}
        )
    }

    LaunchedEffect(uiState, currentStep) {
        if (uiState is SignupConfigurationUiState.Success && currentStep == 3) {
            currentStep = 4
        }
    }
}

@Composable
private fun SignupBottomActions(
    currentStep: Int,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    navigateToLogin: () -> Unit,
    onStartExperience: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        when (currentStep) {
            1 -> {
                Column {
                    DefaultButton(
                        text = stringResource(R.string.signup_next),
                        onClick = onNext,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(com.groupec.salesb.core.ui.R.string.signup_already_have_account),
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(com.groupec.salesb.core.ui.R.string.signup_login),
                            style = MaterialTheme.typography.titleSmall,
                            color = Primary,
                            modifier = Modifier.clickable(onClick = navigateToLogin)
                        )
                    }
                }
            }

            2, 3 -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    DefaultButton(
                        onClick = onPrevious,
                        textcolor = Primary,
                        containerColor = White,
                        modifier = Modifier.weight(1f),
                        enabled = !isLoading,
                        border = BorderStroke(1.dp, Silver2),
                        text = stringResource(R.string.signup_previous)
                    )
                    DefaultButton(
                        text = if (currentStep == 3) stringResource(R.string.signup_finish) else stringResource(R.string.signup_next),
                        onClick = onNext,
                        isLoading = isLoading,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            else -> {
                DefaultButton(
                    text = stringResource(R.string.signup_start_experience),
                    onClick = onStartExperience,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
