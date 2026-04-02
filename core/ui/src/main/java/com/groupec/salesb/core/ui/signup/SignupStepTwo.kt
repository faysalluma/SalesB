package com.groupec.salesb.core.ui.signup

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppExposedDropdownMenuWithError
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.isValidEmail
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.ui.AddImage
import com.groupec.salesb.core.ui.R
import java.io.File


@Composable
fun SignupStepTwo(
    state: SignupStepTwoFormState,
    showErrors: Boolean,
    uri: Uri?,
    onUriChange: (Uri?) -> Unit,
    deleteImageFromCache: (String) -> Unit,
    typeCompanyItems: List<String>,
    typeCompanyState: String,
    ontypeCompanyState: (String) -> Unit,
    onValueChange: (SignupStepTwoFormState) -> Unit
) {
    val context = LocalContext.current
    Text(
        stringResource(R.string.signup_company_title),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(top = 8.dp)
    )
    Text(
        stringResource(R.string.signup_step_2),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge, color = Color.Gray,
        modifier = Modifier.padding(bottom = 18.dp)
    )

    //image to show bottom sheet
    AddImage(
        directory = File(context.cacheDir, "images"),
        uri = uri,
        textImageRes = R.string.add_logo,
        onSetUri = {
            onUriChange(it)
            if (it == null) onValueChange(state.copy(image = "")) // Notify image delete for external api
        },
        /*  upload = {
              viewModel.uploadImage(it)
          },*/
        deleteFile = deleteImageFromCache
    )

    SignUpStepTwoForm(
        state = state,
        typeCompanyItems = typeCompanyItems,
        typeCompanyState = typeCompanyState,
        ontypeCompanyState = ontypeCompanyState,
        onValueChange = onValueChange,
        showErrors = showErrors
    )
}

@Composable
private fun SignUpStepTwoForm(
    state: SignupStepTwoFormState,
    typeCompanyItems: List<String>,
    typeCompanyState: String,
    ontypeCompanyState: (String) -> Unit,
    onValueChange: (SignupStepTwoFormState) -> Unit,
    showErrors: Boolean
) {
    AppTextField(
        value = state.companyName,
        onChange = { onValueChange(state.copy(companyName = it)) },
        label = stringResource(R.string.signup_company_name_required),
        placeholder = stringResource(R.string.signup_company_name_placeholder),
        isError = showErrors && state.companyName.isBlank(),
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppExposedDropdownMenuWithError(
        modifier = Modifier
            .fillMaxWidth(),
        items = typeCompanyItems,
        value = typeCompanyState,
        label = stringResource(R.string.signup_company_type),
        isError = showErrors && typeCompanyState == typeCompanyItems.firstOrNull().orEmpty(),
        onValueChange = {
            ontypeCompanyState(it)
        }
    ) { index, item ->
        val backendValue = when (index) {
            1 -> 0
            2 -> 1
            else -> -1
        }
        onValueChange(state.copy(companyType = backendValue))
    }

    AppTextField(
        value = state.ifu,
        onChange = { onValueChange(state.copy(ifu = it)) },
        label = stringResource(R.string.signup_ifu_optional),
        placeholder = stringResource(R.string.signup_ifu_optional_placeholder),
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.address,
        onChange = { onValueChange(state.copy(address = it)) },
        label = stringResource(R.string.signup_address_optional),
        placeholder = stringResource(R.string.adresse_placeholder),
        leadingIcon = { Icon(Icons.Outlined.LocationOn, null) },
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.phone,
        onChange = { onValueChange(state.copy(phone = it)) },
        label = stringResource(R.string.signup_phone_optional),
        placeholder = "+000 00 00 00 00",
        leadingIcon = { Icon(Icons.Default.Phone, null) },
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.email,
        onChange = { onValueChange(state.copy(email = it)) },
        label = stringResource(R.string.signup_company_email_optional),
        placeholder = "contact@company.com",
        fieldType = FieldType.Email,
        leadingIcon = { Icon(Icons.Default.Email, null) },
        fieldColor = White,
        isError = showErrors && (state.email.isNotBlank() && !isValidEmail(state.email)),
        supportingText = if (showErrors && state.email.isNotEmpty() && !isValidEmail(state.email)) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(com.groupec.salesb.core.designsystem.R.string.invalid_email),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else {
            null
        },
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.website,
        onChange = { onValueChange(state.copy(website = it)) },
        label = stringResource(R.string.signup_website_optional),
        leadingIcon = { Icon(AppIcons.Public, null) },
        fieldColor = White,
        modifier = Modifier.fillMaxWidth()
    )
}

data class SignupStepTwoFormState(
    val companyName: String = "",
    val companyType: Int = -1,
    val email: String = "",
    val address: String = "",
    val phone: String = "",
    val ifu: String = "",
    val website: String = "",
    val image: String = ""
) {
    fun isValid(): Boolean {
        val isRequiredValid = companyName.isNotBlank() && (companyType == 0 || companyType == 1)
        val isEmailValid = email.isBlank() || (email.isNotBlank() && isValidEmail(email))
        return isRequiredValid && isEmailValid
    }
}
