package com.groupec.feature.productdetail

import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Product

@Composable
fun ProductDetailScreen(
    product: Product?,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    AppHeadLine(
        text = stringResource(R.string.detail_title),
        trailingContent = {
            Text(
                stringResource(R.string.btn_cancel),
                color = Primary,
                modifier = Modifier.clickable {  }
            )

        }
    )
}

