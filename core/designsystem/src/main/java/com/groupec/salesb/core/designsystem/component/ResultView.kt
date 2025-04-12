package com.groupec.salesb.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.icon.AppIcons.Check
import com.groupec.salesb.core.designsystem.icon.AppIcons.Close
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Red

enum class Type {
    Success,
    Error
}

@Composable
fun ResultView(
    type: Type,
    title: String? = null,
    message: String? = null,
    onAction: (() -> Unit) ? = null
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = if (type == Type.Success) Check else Close,
                contentDescription = "Result Icon",
                tint = Color.White,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(if (type == Type.Success) Green else Red)
                    .padding(8.dp)
            )
            TitleHeader(
                title = if (type == Type.Success) {
                    title ?: stringResource(R.string.success_title)
                } else {
                    title ?: stringResource(R.string.error)
                },
                color = if (type == Type.Success) Green else Red,
                textAlign = TextAlign.Center
            )
            message?.let {
                TextNormal(
                    text = it,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            onAction?.let {
                DefaultButton(
                    text = stringResource(R.string.close),
                    modifier = Modifier.fillMaxWidth(0.5f).padding(top = 24.dp),
                    containerColor = if (type == Type.Success) Green else Red,
                    onClick = it
                )
            }
        }
    }
}

@Preview
@Composable
fun ResultCardPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        ResultView(
            type = Type.Success,
            message = "Details"
        )
    }
}