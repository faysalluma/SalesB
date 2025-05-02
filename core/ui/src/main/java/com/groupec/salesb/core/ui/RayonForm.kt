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
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun RayonForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    rayons: RayonDataForm,
    onRayonDataChanged: (RayonDataForm) -> Unit,
    onSubmitForm: (rayon: RayonDataForm) -> Unit
) {

    var isLibelleError by remember { mutableStateOf(false) }

    val submitAction = {
        isLibelleError = rayons.libelle.isEmpty()
        if (!isLibelleError) {
            // Submit the form
            onSubmitForm(rayons)
        }
    }


    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = rayons.libelle,
            onChange = { data ->
                onRayonDataChanged(rayons.copy(libelle = data))
                if (isLibelleError) isLibelleError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_libelle),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_libelle)
            ),
            isError = isLibelleError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = rayons.description,
            onChange = { data ->
                onRayonDataChanged(rayons.copy(description = data))
            },
            label = stringResource(id = R.string.label_desc),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_desc)
            ),
            fieldColor = White,
            maxLines = 5,
            singleLine = false,
            keyboardAction = KeyboardAction.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
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

data class RayonDataForm(
    val id: String = "",
    val libelle: String = "",
    val description: String = ""
)