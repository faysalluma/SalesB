package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.component.AppCustomBottomSheet
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.TitleLarge

@Composable
fun ProFeatureBottomSheet(
    onDismiss: () -> Unit,
    onUpgradeClick: () -> Unit
) {
    AppCustomBottomSheet(
        onDismiss = onDismiss,
        header = stringResource(R.string.pro_feature_required_title)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleLarge(
                title = stringResource(R.string.pro_feature_required_message),
                textAlign = TextAlign.Center
            )

            DefaultButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.pro_feature_required_cta),
                onClick = onUpgradeClick
            )
        }
    }
}
