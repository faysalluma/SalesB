package com.groupec.feature.updatebusinessinfo

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.feature.updatebusinessinfo.R
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenuWithError
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.normalizeDecimalSeparator
import com.groupec.salesb.core.ui.AddImage
import java.io.File
import com.groupec.salesb.core.ui.R as UiR

@Composable
fun UpdateBusinessInfoScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    viewModel: UpdateBusinessInfoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val uiState by viewModel.updateBusinessUiState.collectAsState()
    val parameter by viewModel.parameterState.collectAsState()
    val isLoading = uiState is FormUIState.Loading

    var formState by remember { mutableStateOf(UpdateBusinessFormData()) }
    var showErrors by remember { mutableStateOf(false) }
    val uri = remember { mutableStateOf<Uri?>(null) }
    var isFormInitialized by remember { mutableStateOf(false) }

    val typeCompanyItems = listOf(
        stringResource(R.string.select),
        stringResource(R.string.sales_and_retailers),
        stringResource(R.string.professional_services)
    )

    var typeCompanyState by remember { mutableStateOf(typeCompanyItems.first()) }

    LaunchedEffect(parameter) {
        if (parameter.id != 0 && !isFormInitialized) {
            uri.value = parameter.logo?.takeIf { it.isNotBlank() }?.let {
                Uri.parse(Constants.UPLOAD_URL.plus(it))
            }
            formState = UpdateBusinessFormData(
                companyName = parameter.raisonsociale,
                companyType = parameter.entreprisetype,
                email = parameter.email.orEmpty(),
                address = parameter.adresse.orEmpty(),
                phone = parameter.telephone.orEmpty(),
                ifu = parameter.ifu.orEmpty(),
                website = parameter.website.orEmpty(),
                devise = parameter.devise,
                tva = if (parameter.tva % 1.0 == 0.0) parameter.tva.toInt().toString() else parameter.tva.toString()
            )
            typeCompanyState = when (parameter.entreprisetype) {
                0 -> typeCompanyItems.getOrNull(1) ?: typeCompanyItems.first()
                1 -> typeCompanyItems.getOrNull(2) ?: typeCompanyItems.first()
                else -> typeCompanyItems.first()
            }
            isFormInitialized = true
        }
    }

    when (uiState) {
        is FormUIState.Success -> {
            LaunchedEffect(uiState) {
                isFormInitialized = false
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = context.getString(UiR.string.product_operate_succesfully)
                    )
                )
                viewModel.resetFlow()
            }
        }
        is FormUIState.Error -> {
            LaunchedEffect(uiState) {
                snackbarHostState.showSnackbar(
                    SnackbarVisualsWithState(
                        message = (uiState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }
        else -> {}
    }

    Column(modifier = modifier.fillMaxSize()) {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 16.dp),
            text = stringResource(R.string.update_business_info_title)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AddImage(
                directory = File(context.cacheDir, "images"),
                uri = uri.value,
                textImageRes = UiR.string.add_logo,
                onSetUri = {
                    uri.value = it
                },
                deleteFile = { filename ->
                    viewModel.deleteImageFromCache(context, filename)
                }
            )

            Column(
                modifier = Modifier.fillMaxWidth(0.82f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AppTextField(
                    value = formState.companyName,
                    onChange = { formState = formState.copy(companyName = it) },
                    label = stringResource(UiR.string.signup_company_name_required),
                    placeholder = stringResource(UiR.string.signup_company_name_placeholder),
                    isError = showErrors && formState.companyName.isBlank(),
                    fieldColor = White,
                    modifier = Modifier.fillMaxWidth()
                )

                AppExposedDropdownMenuWithError(
                    modifier = Modifier.fillMaxWidth(),
                    items = typeCompanyItems,
                    value = typeCompanyState,
                    label = stringResource(UiR.string.signup_company_type),
                    isError = showErrors && typeCompanyState == typeCompanyItems.first(),
                    onValueChange = { typeCompanyState = it }
                ) { index, _ ->
                    val backendValue = when (index) {
                        1 -> 0
                        2 -> 1
                        else -> -1
                    }
                    formState = formState.copy(companyType = backendValue)
                }

                AppTextField(
                    value = formState.ifu,
                    onChange = { formState = formState.copy(ifu = it) },
                    label = stringResource(UiR.string.signup_ifu_optional),
                    placeholder = stringResource(UiR.string.signup_ifu_optional_placeholder),
                    fieldColor = White,
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = formState.address,
                    onChange = { formState = formState.copy(address = it) },
                    label = stringResource(UiR.string.signup_address_optional),
                    placeholder = stringResource(UiR.string.adresse_placeholder),
                    leadingIcon = { Icon(Icons.Outlined.LocationOn, null) },
                    fieldColor = White,
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = formState.phone,
                    onChange = { formState = formState.copy(phone = it) },
                    label = stringResource(UiR.string.signup_phone_optional),
                    placeholder = "+000 00 00 00 00",
                    leadingIcon = { Icon(Icons.Default.Phone, null) },
                    fieldColor = White,
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = formState.email,
                    onChange = { formState = formState.copy(email = it) },
                    label = stringResource(UiR.string.signup_company_email_optional),
                    placeholder = "contact@company.com",
                    fieldType = FieldType.Email,
                    leadingIcon = { Icon(Icons.Default.Email, null) },
                    fieldColor = White,
                    isError = showErrors && formState.email.isNotBlank() && !isValidEmail(formState.email),
                    modifier = Modifier.fillMaxWidth()
                )

                AppTextField(
                    value = formState.website,
                    onChange = { formState = formState.copy(website = it) },
                    label = stringResource(UiR.string.signup_website_optional),
                    leadingIcon = { Icon(AppIcons.Public, null) },
                    fieldColor = White,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AppTextField(
                        value = formState.devise,
                        onChange = { newValue ->
                            if (newValue.length <= 5) {
                                formState = formState.copy(devise = newValue)
                            }
                        },
                        label = stringResource(UiR.string.signup_currency),
                        placeholder = stringResource(UiR.string.signup_currency_placeholder),
                        isError = showErrors && formState.devise.isBlank(),
                        fieldColor = White,
                        modifier = Modifier.weight(1f)
                    )

                    AppTextField(
                        value = formState.tva,
                        onChange = { newValue ->
                            if (newValue.length <= 5) {
                                formState = formState.copy(tva = newValue.normalizeDecimalSeparator())
                            }
                        },
                        label = stringResource(UiR.string.signup_vat),
                        placeholder = stringResource(UiR.string.signup_vat_placeholder),
                        isError = showErrors && formState.tva.isBlank(),
                        fieldColor = White,
                        fieldType = FieldType.Number,
                        modifier = Modifier.weight(1f)
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    DefaultButton(
                        text = stringResource(com.groupec.salesb.core.designsystem.R.string.btn_save),
                        isLoading = isLoading,
                        onClick = {
                            showErrors = true
                            if (isFormValid(formState, typeCompanyState, typeCompanyItems)) {
                                viewModel.updateBusinessInfo(formState, uri.value)
                                focusManager.clearFocus()
                            }
                        }
                    )
                }
            }
        }
    }
}

private fun isFormValid(
    form: UpdateBusinessFormData,
    typeCompanyState: String,
    typeCompanyItems: List<String>
): Boolean {
    val isRequiredValid = form.companyName.isNotBlank() && typeCompanyState != typeCompanyItems.first()
    val isEmailValid = form.email.isBlank() || isValidEmail(form.email)
    val isTvaValid = form.tva.isNotBlank() && form.tva.toDoubleOrNull() != null
    return isRequiredValid && isEmailValid && form.devise.isNotBlank() && isTvaValid
}
