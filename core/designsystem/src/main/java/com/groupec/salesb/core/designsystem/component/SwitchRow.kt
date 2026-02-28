package com.groupec.salesb.core.designsystem.component

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary


@Composable
fun SwitchRow(
    verticalpadding: Dp = 20.dp,
    showInformation: (() -> Unit)? = null,
    option: HandleServiceOption,
    onCheckedChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp, vertical = verticalpadding),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = stringResource(option.titleRes),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(option.descriptionRes),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row {
            Switch(
                checked = option.checked,
                onCheckedChange = onCheckedChanged,
                enabled = option.enabled
            )
            showInformation?.let { onClick ->
                IconButton(
                    onClick = onClick,
                    modifier = Modifier.padding(start = 6.dp)
                ) {
                    Icon(
                        modifier = Modifier.size(72.dp),
                        tint = Green,
                        imageVector = AppIcons.MoreInfo,
                        contentDescription = "Information"
                    )
                }
            }
        }
    }
}

data class HandleServiceOption(
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int,
    val checked: Boolean,
    val enabled: Boolean,
    val type: HandleServiceToggleType
)

enum class HandleServiceToggleType {
    SERVICE_VIEW,
    SHOW_IMAGE_ON_PRODUCT,
    USE_INT_FOR_PRICE_AND_AMOUNT,
    ACTIVE_PAYMENT_MODE,
    ACTIVE_PRINTER
}
