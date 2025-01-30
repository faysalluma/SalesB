package com.groupec.salesb.core.designsystem.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


@Composable
fun CustomSnackBar(
    data: SnackbarData,
    containerColor: Color = SnackbarDefaults.color,
    contentColor: Color = SnackbarDefaults.contentColor,
) {
    Snackbar(
        containerColor = containerColor, // Background color
        contentColor = contentColor, // Text color
        actionContentColor = contentColor, // Action button color
        dismissActionContentColor = contentColor, // Dismiss button color
        dismissAction = {
            // Affichage du bouton "X" si `withDismissAction` est activé
            IconButton(onClick = { data.dismiss() }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Dismiss",
                    tint = contentColor
                )
            }
        }
    ) {
        Text(data.visuals.message)
    }
}

class SnackbarVisualsWithState(
    override val message: String,
    val isError: Boolean = false
) : SnackbarVisuals {
    override val actionLabel: String?
        get() = null
    override val duration: SnackbarDuration
        get() = if (isError) SnackbarDuration.Indefinite else SnackbarDuration.Long
    override val withDismissAction: Boolean
        get() = false
}

