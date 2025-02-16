package com.groupec.salesb.core.designsystem.component

import android.util.Patterns
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

enum class KeyboardAction {
    Next,
    Done,
    Unspecified
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
    value: String = "",
    onChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = TextFieldDefaults.shape,
    label: String ? = null,
    placeholder: String = "",
    fieldType: FieldType = FieldType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    fieldColor: Color? = null,
    isError: Boolean = false,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardAction: KeyboardAction = KeyboardAction.Next,
    submitAction: (() -> Unit)? = null
) {
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var textValue by remember { mutableStateOf("") }

    val passwordTrailingIcon = @Composable {
        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
            Icon(
                painter = if (isPasswordVisible) {
                    painterResource(id = R.drawable.visibility_off)
                } else {
                    painterResource(id = R.drawable.visibility)
                },
                contentDescription = null,
                tint = Primary
            )
        }
    }


    val supportingTextValue: (@Composable () -> Unit)? = when {
        isError -> supportingText ?: if (textValue.isEmpty()) {
            {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.required_field),
                    color = MaterialTheme.colorScheme.error
                )
            }
        } else null
        else -> null
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
        shape = shape,
        keyboardOptions = KeyboardOptions(
            imeAction = when (keyboardAction) {
                KeyboardAction.Next -> {
                    ImeAction.Next
                }
                KeyboardAction.Done -> {
                    ImeAction.Done
                }
                else -> {
                    ImeAction.Unspecified
                }
            },
            keyboardType = when (fieldType) {
                FieldType.Text -> KeyboardType.Text
                FieldType.Number -> KeyboardType.Number
                FieldType.Email -> KeyboardType.Email
                FieldType.Password -> KeyboardType.Password
            }
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                if (keyboardAction == KeyboardAction.Next) {
                    focusManager.moveFocus(FocusDirection.Down)
                }
            },
            onDone = {
                if (keyboardAction == KeyboardAction.Done) {
                    if (submitAction != null) {
                        submitAction()
                    }
                }
            }
        ),
        placeholder = { Text(placeholder) },
        label = label?.let { { Text(it) } },
        maxLines = maxLines,
        singleLine = singleLine,
        colors = when {
            fieldColor != null -> {
                if (shape != TextFieldDefaults.shape) {
                    ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = fieldColor,
                        unfocusedContainerColor = fieldColor,
                        focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                        unfocusedIndicatorColor = Color.Transparent // Remove underline when unfocused
                    )
                } else {
                    ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = fieldColor,
                        unfocusedContainerColor = fieldColor
                    )
                }
            }
            else -> {
                if (shape != TextFieldDefaults.shape) {
                    ExposedDropdownMenuDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent, // Remove underline when focused
                        unfocusedIndicatorColor = Color.Transparent // Remove underline when unfocused
                    )
                } else {
                    ExposedDropdownMenuDefaults.textFieldColors()
                }
            }
        },
        visualTransformation = if (fieldType == FieldType.Password) {
            if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        isError = isError,
        supportingText = supportingTextValue,
    )
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview
@Composable
fun TextFieldPreview() {
    Column {
        AppTextField(
            onChange = {},
            label = stringResource(id = R.string.label_email),
            placeholder = stringResource(id = R.string.enter_your_email),
            fieldType = FieldType.Email,
            modifier = Modifier.padding(16.dp)
        )
    }
}