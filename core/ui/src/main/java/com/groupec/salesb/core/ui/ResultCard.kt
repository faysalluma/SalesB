package com.groupec.salesb.core.ui

import androidx.compose.runtime.Composable
import com.groupec.salesb.core.designsystem.component.ResultView
import com.groupec.salesb.core.designsystem.component.Type


@Composable
fun SuccessCard(
    title: String? = null,
    message: String? = null,
    onAction: (() -> Unit) ? = null
) {
    ResultView(
        type = Type.Success,
        title = title,
        message = message,
        onAction = onAction
    )
}

@Composable
fun ErrorCard(
    title: String? = null,
    message: String? = null,
    onAction: (() -> Unit) ? = null
) {
    ResultView(
        type = Type.Error,
        title = title,
        message = message,
        onAction = onAction
    )
}