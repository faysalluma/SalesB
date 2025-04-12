package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.ResultView
import com.groupec.salesb.core.designsystem.component.TextNormal
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.Type
import com.groupec.salesb.core.designsystem.icon.AppIcons.Check
import com.groupec.salesb.core.designsystem.icon.AppIcons.Close
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Red



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