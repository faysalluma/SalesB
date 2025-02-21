package com.groupec.salesb.core.designsystem.component

import android.app.Activity
import android.os.Handler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.utils.Constants

@Composable
fun AppAlertInfoDialog(
    setShowDialog: ((Boolean) -> Unit)? = null,
    title: String?,
    titleColor: Color = LocalContentColor.current,
    icon: Painter? = null,
    tintIcon: Color = LocalContentColor.current,
    message: String? = null,
    confirmButtonText: String? = null,
    confirmButtonColor: Color = Primary,
    onConfirmButton: (() -> Unit)? = null,
    disableConfirmActionDismiss: Boolean = false,
    isLoading: Boolean = false,
    dismissButtonText: String? = null,
    onDismissButton: (() -> Unit)? = null,
    closing: Activity? = null
) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            setShowDialog?.let { it(false) }
        },
        icon = {
            icon?.let {
                Icon(
                    it,
                    contentDescription = null,
                    tint = tintIcon,
                    modifier = Modifier.size(70.dp)
                )
            }
        },
        title = {
            title?.let {
                Text(it, color = titleColor)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                message?.let {
                    Text(text = it, textAlign = TextAlign.Center)
                }
                if (isLoading) {
                    AppLoadingScreen(modifier = Modifier.wrapContentWidth())
                }
            }
        },
        confirmButton = {
            onConfirmButton?.let { onClickAction ->
                val buttonModifier =
                    if (onDismissButton == null) Modifier.fillMaxWidth() else Modifier.wrapContentWidth()
                DefaultButton(
                    modifier = buttonModifier,
                    containerColor = confirmButtonColor,
                    text = confirmButtonText ?: stringResource(R.string.btn_confirm),
                    onClick = {
                        onClickAction()
                        if (!disableConfirmActionDismiss) setShowDialog?.let { it(false) }
                        closing?.finish()
                    }
                )
            }
        },
        dismissButton = {
            onDismissButton?.let { onClickAction ->
                DefaultButton(
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(end = 6.dp),
                    containerColor = Silver,
                    textcolor = Color.Black,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W400),
                    text = dismissButtonText ?: stringResource(R.string.btn_cancel),
                    onClick = {
                        onClickAction()
                        setShowDialog?.let { it(false) }
                    }
                )
            }
        },
        modifier = Modifier.padding(20.dp)
    )

    // Close dialog after 3.5s
    closing?.let {
        val handler = Handler()
        handler.postDelayed({
            setShowDialog?.let { it(false) }
            it.finish()
        }, Constants.DELAY_DIALOG_DISMISS)
    }
}
