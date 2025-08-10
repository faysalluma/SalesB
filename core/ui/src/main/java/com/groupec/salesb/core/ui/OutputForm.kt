package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.normalizeDecimalSeparator

@Composable
fun OutputForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    outputs: OutputDataForm,
    onOutputDataChanged: (OutputDataForm) -> Unit,
    onSubmitForm: (output: OutputDataForm) -> Unit
) {

    var isDescError by remember { mutableStateOf(false) }
    var isPrixError by remember { mutableStateOf(false) }

    val submitAction = {
        isDescError = outputs.description.isEmpty()
        isPrixError = outputs.prix.isEmpty()
        if (!isDescError && !isPrixError) {
            // Submit the form
            onSubmitForm(outputs)
        }
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        AppTextField(
            value = outputs.description,
            onChange = { data ->
                onOutputDataChanged(outputs.copy(description = data))
                if (isDescError) isDescError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_desc_require),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_desc)
            ),
            fieldColor = White,
            maxLines = 5,
            singleLine = false,
            isError = isDescError,
            keyboardAction = KeyboardAction.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        AppTextField(
            value = outputs.prix,
            onChange = { data ->
                onOutputDataChanged(outputs.copy(prix = data.normalizeDecimalSeparator()))
                if (isPrixError) isPrixError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_price),
            fieldType = FieldType.Number,
            isError = isPrixError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DefaultButton(
                onClick = submitAction,
                text = stringResource(id = R.string.btn_save),
                isLoading = isLoading
            )
        }
    }
}

data class OutputDataForm(
    val id: String = "",
    val prix: String = "",
    val description: String = ""
)