package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.designsystem.component.IconTextButton
import com.groupec.salesb.core.designsystem.component.Position
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.component.UnderlinedTextButton
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Red
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.White

@Composable
fun OfflineErrorScreen(
    modifier: Modifier = Modifier,
    onRetry: () -> Unit = {},
    onOpenNetworkSettings: () -> Unit = {}
) {
    val description = buildAnnotatedString {
        append(stringResource(R.string.offline_error_message_prefix))
        withStyle(SpanStyle(color = Primary, fontWeight = FontWeight.SemiBold)) {
            append(" SalesB.")
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OfflineIllustration()

            Spacer(modifier = Modifier.height(44.dp))

            TitleLarge(
                title = stringResource(R.string.offline_error_title),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = description,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(36.dp))

            IconTextButton(
                text = stringResource(R.string.retry),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null
                    )
                },
                position = Position.Left,
                shape = RoundedCornerShape(14.dp),
                onClick = onRetry
            )

            Spacer(modifier = Modifier.height(18.dp))

            UnderlinedTextButton(
                text = stringResource(R.string.offline_network_settings),
                onClick = onOpenNetworkSettings
            )
        }
    }
}

@Composable
private fun OfflineIllustration(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.TopEnd
    ) {
        Surface(
            modifier = Modifier
                .size(144.dp)
                .shadow(
                    elevation = 28.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = Primary.copy(alpha = 0.10f),
                    spotColor = Primary.copy(alpha = 0.12f)
                ),
            shape = RoundedCornerShape(28.dp),
            color = White
        ) {
            Box(contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.size(84.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFFE7F0FF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        SlashedIcon(
                            icon = AppIcons.Public,
                            tint = Red,
                            slashTint = Red,
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlashedIcon(
    icon: ImageVector,
    tint: Color,
    slashTint: Color,
    modifier: Modifier = Modifier
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.matchParentSize()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun OfflineErrorScreenPreview() {
    SalesBAppTheme {
        OfflineErrorScreen()
    }
}
