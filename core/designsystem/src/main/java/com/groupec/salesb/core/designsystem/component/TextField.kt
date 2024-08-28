package com.groupec.salesb.core.designsystem.component

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Primary

enum class FieldType {
    Text,
    Number,
    Email,
    Password
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String = "",
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    placeholder: String,
    fieldType: FieldType,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine : Boolean = true,
    fieldColor : Color ? = null,
    isError: Boolean = false,
    ) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("") }

    val passwordTrailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                painter =  if (isPasswordVisible) {
                    painterResource(id = R.drawable.visibility_off)
                } else {
                    painterResource(id = R.drawable.visibility)
                },
                contentDescription = null,
                tint = Primary
            )
        }
    }

    val supportingText : @Composable (() -> Unit)? = if (isError) {
        @Composable {
            if (fieldType == FieldType.Email && textValue.isNotEmpty() && !isValidEmail(textValue)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.invalid_email),
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.required_field),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    } else {
        null
    }

    // Validate email if the field type is Email
    val onValueChange = { newValue: String ->
        textValue = newValue // Get value from text field
        onChange(newValue)
    }


    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon ?: if (fieldType == FieldType.Password) {
                passwordTrailingIcon
            } else {
                null
            },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = when (fieldType) {
                FieldType.Text -> KeyboardType.Text
                FieldType.Number -> KeyboardType.Number
                FieldType.Email -> KeyboardType.Email
                FieldType.Password -> KeyboardType.Password
            }
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) }
        ),
        /*keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Password
        ),
        keyboardActions = KeyboardActions(
            onDone = { submitAction() }
        ),*/
        placeholder = { Text(placeholder) },
        label = { Text(label) },
        singleLine = singleLine,
        colors = if (fieldColor != null){
            ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = fieldColor,
                unfocusedContainerColor = fieldColor
            )
        } else {
            ExposedDropdownMenuDefaults.textFieldColors()
        },
        visualTransformation = if (fieldType == FieldType.Password) {
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = isError,
        supportingText = supportingText,
    )
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview
@Composable
fun TextFieldPreview(){
    Column {
        AppTextField(
            onChange = {} ,
            label = stringResource(id = R.string.label_email),
            placeholder = stringResource(id = R.string.enter_your_email) ,
            fieldType = FieldType.Email,
            modifier = Modifier.padding(16.dp)
        )
    }
}