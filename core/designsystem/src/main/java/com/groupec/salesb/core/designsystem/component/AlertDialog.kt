package com.groupec.salesb.core.designsystem.component

import android.app.Activity
import android.os.Handler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.utils.Constants

@Composable
fun AppAlertInfoDialog(
    title: String?,
    icon: Painter?,
    tintIcon: Color?,
    message: String,
    textButton: String,
    closing: Activity?
) {
    val openDialog = remember { mutableStateOf(true) }

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                // openDialog.value = false
            },
            icon = {
                icon?.let {
                    Icon(
                        it,
                        contentDescription = null,
                        tint = tintIcon ?: Primary,
                        modifier = Modifier.size(70.dp)
                    )
                }
            },
            title = {
                title?.let {
                    Text(it, color = Primary)
                }
            },
            text = {
                Text(text = message, textAlign = TextAlign.Center)
            },
            confirmButton = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DefaultButton(onClick = {
                        openDialog.value = false
                        closing?.finish()
                    }, text = textButton)
                }
            },
            dismissButton = {},
            modifier = Modifier.padding(20.dp)
        )
    }

    // Close dialog after 3.5s
    closing?.let {
        val handler = Handler()
        handler.postDelayed({
            if (openDialog.value) {
                openDialog.value = false
                it.finish()
            }
        }, Constants.DELAY_DIALOG_DISMISS)
    }
}
