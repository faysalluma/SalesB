package com.groupec.feature.productlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.feature.product.R
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.TitleMedium
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.ProductCardList

@Composable
fun ProductListScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = hiltViewModel(),
    onViewDetail: (Product) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val products = viewModel.pagedProducts.collectAsLazyPagingItems()

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ){
        AppHeadLine(
            text = stringResource(R.string.head_title),
            leadingContent = {
                Icon(imageVector = AppIcons.FilterList, contentDescription = "Filter List")
            }
        )

        // Barre de recherche
        AppTextField(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            value = searchQuery,
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = "Search icon"
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(
                            imageVector = AppIcons.Close,
                            contentDescription = "Clear text"
                        )
                    }
                }
            },
            onChange = { viewModel.updateSearchQuery(it) },
            placeholder = stringResource(R.string.search_product_place_holder),
            fieldType = FieldType.Text,
            fieldColor = Silver,
            shape = RoundedCornerShape(28.dp)
        )

        // Liste paginée
        ProductCardList(
            products = products,
            isSearching = isSearching,
            onViewDetail = onViewDetail,
            onDelete = viewModel::deleteProduct
        )
    }
}
