package com.groupec.feature.signup.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.feature.signup.R
import com.groupec.feature.signup.SignupStepOneFormState
import com.groupec.feature.signup.SignupStepTwoFormState
import com.groupec.feature.signup.designsystem.SignupPreferenceCard
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary

@Composable
fun SignupStepOneContent(
    state: SignupStepOneFormState,
    showErrors: Boolean,
    onValueChange: (SignupStepOneFormState) -> Unit,
    onLoginClick: () -> Unit
) {
    Text(stringResource(R.string.signup_create_account), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
    Text(stringResource(R.string.signup_step_1), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

    AppTextField(
        value = state.fullName,
        onChange = { onValueChange(state.copy(fullName = it)) },
        label = stringResource(R.string.signup_name),
        placeholder = stringResource(R.string.signup_full_name_placeholder),
        leadingIcon = { Icon(Icons.Default.Person, null) },
        isError = showErrors && state.fullName.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.email,
        onChange = { onValueChange(state.copy(email = it)) },
        label = stringResource(R.string.signup_email),
        placeholder = stringResource(R.string.signup_email_placeholder),
        fieldType = FieldType.Email,
        leadingIcon = { Icon(Icons.Default.Email, null) },
        isError = showErrors && state.email.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.password,
        onChange = { onValueChange(state.copy(password = it)) },
        label = stringResource(R.string.signup_password),
        placeholder = "••••••••",
        fieldType = FieldType.Password,
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        isError = showErrors && state.password.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.confirmPassword,
        onChange = { onValueChange(state.copy(confirmPassword = it)) },
        label = stringResource(R.string.signup_confirm_password),
        placeholder = "••••••••",
        fieldType = FieldType.Password,
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        isError = showErrors && state.confirmPassword.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.signup_already_have_account))
        Text(
            text = stringResource(R.string.signup_login),
            color = Primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onLoginClick)
        )
    }
}

@Composable
fun SignupStepTwoContent(
    state: SignupStepTwoFormState,
    showErrors: Boolean,
    onValueChange: (SignupStepTwoFormState) -> Unit
) {
    Text(stringResource(R.string.signup_company_title), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)

    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .background(Color(0xFFF4C27A))
                .border(2.dp, Color.Gray, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(AppIcons.Public, contentDescription = null, tint = Color.White)
        }
    }

    Text(
        text = stringResource(R.string.signup_add_logo) + "\n" + stringResource(R.string.signup_click_to_upload),
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center,
        color = Primary
    )

    AppTextField(
        value = state.companyName,
        onChange = { onValueChange(state.copy(companyName = it)) },
        label = stringResource(R.string.signup_company_name_required),
        placeholder = stringResource(R.string.signup_company_name_placeholder),
        isError = showErrors && state.companyName.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.companyType,
        onChange = { onValueChange(state.copy(companyType = it)) },
        label = stringResource(R.string.signup_company_type),
        placeholder = stringResource(R.string.signup_company_type_placeholder),
        leadingIcon = { Icon(AppIcons.Language, null) },
        isError = showErrors && state.companyType.isBlank(),
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.email,
        onChange = { onValueChange(state.copy(email = it)) },
        label = stringResource(R.string.signup_company_email_optional),
        placeholder = "contact@company.com",
        fieldType = FieldType.Email,
        leadingIcon = { Icon(Icons.Default.Email, null) },
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.address,
        onChange = { onValueChange(state.copy(address = it)) },
        label = stringResource(R.string.signup_address_optional),
        placeholder = "123 Rue des Affaires, Ville",
        leadingIcon = { Icon(Icons.Outlined.LocationOn, null) },
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.phone,
        onChange = { onValueChange(state.copy(phone = it)) },
        label = stringResource(R.string.signup_phone_optional),
        placeholder = "+229 00 00 00 00",
        leadingIcon = { Icon(Icons.Default.Phone, null) },
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.ifu,
        onChange = { onValueChange(state.copy(ifu = it)) },
        label = stringResource(R.string.signup_ifu_optional),
        placeholder = "IFU",
        modifier = Modifier.fillMaxWidth()
    )

    AppTextField(
        value = state.website,
        onChange = { onValueChange(state.copy(website = it)) },
        label = stringResource(R.string.signup_website_optional),
        placeholder = "https://your-company.com",
        leadingIcon = { Icon(AppIcons.Public, null) },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun SignupStepThreeContent(onInfoClick: () -> Unit) {
    var wholeQuantities by rememberSaveable { mutableStateOf(true) }
    var productImages by rememberSaveable { mutableStateOf(true) }
    var showPaymentMode by rememberSaveable { mutableStateOf(false) }
    var printService by rememberSaveable { mutableStateOf(true) }

    Text(stringResource(R.string.signup_step_3), style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
    Text(stringResource(R.string.signup_subtitle_preferences), style = MaterialTheme.typography.bodyLarge, color = Color.Gray)

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = "Euro (€)",
                onChange = {},
                label = stringResource(R.string.signup_currency),
                modifier = Modifier.fillMaxWidth()
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            AppTextField(
                value = "18",
                onChange = {},
                label = stringResource(R.string.signup_vat),
                fieldType = FieldType.Number,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    Text(
        text = stringResource(R.string.signup_display_options),
        color = Color(0xFF8B9BB4),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 8.dp)
    )

    SignupPreferenceCard(
        title = stringResource(R.string.signup_whole_quantities),
        description = stringResource(R.string.signup_whole_quantities_desc),
        checked = wholeQuantities,
        onCheckedChange = { wholeQuantities = it }
    )
    SignupPreferenceCard(
        title = stringResource(R.string.signup_product_images),
        description = stringResource(R.string.signup_product_images_desc),
        checked = productImages,
        onCheckedChange = { productImages = it }
    )
    SignupPreferenceCard(
        title = stringResource(R.string.signup_show_payment_mode),
        description = stringResource(R.string.signup_show_payment_mode_desc),
        checked = showPaymentMode,
        onCheckedChange = { showPaymentMode = it }
    )
    SignupPreferenceCard(
        title = stringResource(R.string.signup_print_service),
        description = stringResource(R.string.signup_print_service_desc),
        checked = printService,
        onCheckedChange = { printService = it },
        trailingInfoAction = onInfoClick
    )
}

@Composable
fun SignupSuccessContent() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(Primary.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Primary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }

    Text(
        stringResource(R.string.signup_welcome_title),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.signup_welcome_message),
        color = Color.Gray,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
    )
    Spacer(modifier = Modifier.height(80.dp))
}
