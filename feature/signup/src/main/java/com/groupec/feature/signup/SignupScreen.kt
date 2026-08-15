package com.groupec.feature.signup

import android.net.Uri
import android.widget.Toast
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
import com.groupec.salesb.core.FeatureAccess
import com.groupec.salesb.core.Result
import com.groupec.salesb.core.Utility
import com.groupec.salesb.core.designsystem.component.AppAlertInfoDialog
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.SignupStepIndicator
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.SignupConfiguration
import com.groupec.salesb.core.model.data.others.paymentTypeFromLabel
import com.groupec.salesb.core.model.data.others.paymentTypeLabels
import com.groupec.salesb.core.model.data.others.paymentTypeValue
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
    val isQuickSignupEnabled = FeatureAccess.isQuickSignupEnabled
    val uiState by viewModel.signupConfigurationUiState.collectAsState()
    val context = LocalContext.current
    val paymentTypeList = paymentTypeLabels(context)
    val firstPaymentTypeValue = paymentTypeList.firstOrNull().orEmpty()
    val defaultCurrencyCode = remember { Utility.defaultCurrencyCodeForCurrentLocale() }
    var paymentTypeState by remember { mutableStateOf(firstPaymentTypeValue) }

    var stepOne by remember { mutableStateOf(SignupStepOneFormState()) }
    var stepTwo by remember(isQuickSignupEnabled) {
        mutableStateOf(
            SignupStepTwoFormState(
                companyType = 0
            )
        )
    }
    var stepThree by remember(isQuickSignupEnabled, defaultCurrencyCode, firstPaymentTypeValue) {
        mutableStateOf(
            SignupStepThreeFormState(
                devise = if (isQuickSignupEnabled) defaultCurrencyCode else "",
                tva = if (isQuickSignupEnabled) "0" else "",
                defaultpayment = firstPaymentTypeValue
            )
        )
    }

    var showStepOneErrors by remember { mutableStateOf(false) }
    var showStepTwoErrors by remember { mutableStateOf(false) }
    var showStepThreeErrors by remember { mutableStateOf(false) }
    var stepOneEmailErrorMessage by remember { mutableStateOf<String?>(null) }
    var isStepOneLoading by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    var uri by remember { mutableStateOf<Uri?>(null) }
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
        if (!isQuickSignupEnabled) {
            SignupStepIndicator(step = currentStep, modifier = Modifier.padding(16.dp))
        }
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
                    subtitle = if (isQuickSignupEnabled) {
                        stringResource(R.string.signup_quick_step_1)
                    } else {
                        stringResource(com.groupec.salesb.core.ui.R.string.signup_step_1)
                    },
                    showErrors = showStepOneErrors,
                    onValueChange = {
                        if (it.email != stepOne.email) {
                            stepOneEmailErrorMessage = null
                        }
                        stepOne = it
                    }
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
                    onValueChange = { stepTwo = it }
                )

                3 -> SignupStepThree(
                   isServiceBusiness = stepTwo.companyType == 1,
                   state = stepThree,
                   showErrors = showStepThreeErrors,
                   paymentTypeState = paymentTypeState,
                   onPaymenTypeSelected = { paymentTypeState = it },
                   onValueChange = { stepThree = it }
                )
                else -> SignupSuccessContent()
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        SignupBottomActions(
            currentStep = currentStep,
            isQuickSignupEnabled = isQuickSignupEnabled,
            isLoading = uiState is SignupConfigurationUiState.Loading,
            isStepOneLoading = isStepOneLoading,
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
                        if (valid) {
                            coroutineScope.launch {
                                isStepOneLoading = true
                                when (val emailCheckResult = viewModel.checkEmailExists(stepOne.email.trim())) {
                                    is Result.Success -> {
                                        if (emailCheckResult.data) {
                                            stepOneEmailErrorMessage = context.getString(
                                                com.groupec.salesb.core.ui.R.string.signup_email_already_taken_error
                                            )
                                            showStepOneErrors = true
                                        } else {
                                            stepOneEmailErrorMessage = null
                                            if (isQuickSignupEnabled) {
                                                stepTwo = stepTwo.copy(companyType = 0)
                                                stepThree = stepThree.copy(
                                                    devise = defaultCurrencyCode,
                                                    tva = "0",
                                                    showInt = 0,
                                                    showProductImage = 1,
                                                    showPaymentMode = 1,
                                                    defaultpayment = firstPaymentTypeValue,
                                                    activePrinter = 0
                                                )
                                                paymentTypeState = firstPaymentTypeValue
                                                currentStep = 4
                                            } else {
                                                currentStep = 2
                                            }
                                            scrollState.animateScrollTo(0)
                                        }
                                    }
                                    is Result.Error -> {
                                        stepOneEmailErrorMessage = context.getString(
                                            com.groupec.salesb.core.ui.R.string.signup_email_validation_error
                                        )
                                        showStepOneErrors = true
                                    }
                                    is Result.Loading -> Unit
                                }
                                isStepOneLoading = false
                            }
                        }
                    }

                    2 -> {
                        val valid = stepTwo.isValid()
                        showStepTwoErrors = !valid
                        if (valid) {
                            stepThree = stepThree.copy(
                                showInt = 0,
                                showProductImage = 1,
                                showPaymentMode = 1
                            )
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
                            currentStep = 4
                            coroutineScope.launch {
                                scrollState.animateScrollTo(0)
                            }
                        }
                    }
                }
            },
            navigateToLogin = navigateToLogin,
            onStartExperience = {
                val paymentTypeValueForSave = if (stepThree.showPaymentMode == 1) {
                    paymentTypeFromLabel(context, stepThree.defaultpayment)?.let(::paymentTypeValue)
                } else {
                    null
                }
                val configuration = SignupConfiguration(
                    fullName = stepOne.fullName,
                    email = stepOne.email,
                    password = stepOne.password,
                    companyName = if (isQuickSignupEnabled) " " else stepTwo.companyName,
                    companyType = 0,
                    companyEmail = if (isQuickSignupEnabled) null else stepTwo.email,
                    address = if (isQuickSignupEnabled) null else stepTwo.address,
                    phone = if (isQuickSignupEnabled) null else stepTwo.phone,
                    ifu = if (isQuickSignupEnabled) null else stepTwo.ifu,
                    website = if (isQuickSignupEnabled) null else stepTwo.website,
                    devise = if (isQuickSignupEnabled) defaultCurrencyCode else stepThree.devise.uppercase().trim(),
                    tva = if (isQuickSignupEnabled) 0.0 else (stepThree.tva.toDoubleOrNull() ?: 0.0),
                    useIntForPriceAndAmount = if (isQuickSignupEnabled) 0 else stepThree.showInt,
                    showImageOnProduct = if (isQuickSignupEnabled) 1 else stepThree.showProductImage,
                    activePaymentMode = if (isQuickSignupEnabled) 1 else stepThree.showPaymentMode,
                    defaultpayment = paymentTypeValueForSave,
                    activePrinter = if (isQuickSignupEnabled) 0 else stepThree.activePrinter
                )
                viewModel.saveInitialConfiguration(configuration, uri)
            }
        )
    }

    LaunchedEffect(stepOneEmailErrorMessage) {
        stepOneEmailErrorMessage
            ?.takeIf { it.isNotBlank() }
            ?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                stepOneEmailErrorMessage = null
            }
    }

    LaunchedEffect(uiState, currentStep) {
        if (uiState is SignupConfigurationUiState.Success && currentStep == 4) {
            navigateToHome()
        }
    }
}

@Composable
private fun SignupBottomActions(
    currentStep: Int,
    isQuickSignupEnabled: Boolean,
    isLoading: Boolean,
    isStepOneLoading: Boolean,
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
                        text = if (isQuickSignupEnabled) {
                            stringResource(R.string.signup_submit)
                        } else {
                            stringResource(R.string.signup_next)
                        },
                        onClick = onNext,
                        isLoading = isStepOneLoading,
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
                    isLoading = isLoading,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
