package com.groupec.feature.productdetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.CardImage
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.ProductForm
import com.groupec.salesb.core.ui.StatisticChart

@Composable
fun ProductDetailScreen(
    product: Product?,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    Column {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 28.dp),
            text = stringResource(R.string.detail_title),
            trailingContent = {
                Text(
                    stringResource(R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {  }
                )

            }
        )

       FormScreen()
    }

}

@Composable
fun FormScreen(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
        ,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CardImage(imageUrl = "", modifier = Modifier.padding(top = 3.dp, bottom = 14.dp)) {

        }
        val categorieItems = listOf("1" to "Cupcake" , "2" to "Donut", "3" to "Eclair")
        ProductForm(
            modifier = Modifier.fillMaxWidth(0.8f),
            categorieItems = categorieItems,
            onSubmitForm = {}
        )
    }
}

