package com.groupec.feature.salelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.groupec.salesb.core.currentDateString
import com.groupec.salesb.core.designsystem.component.AppCustomDialog
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DatePickerFieldToModal
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.ui.SaleCardList
import com.groupec.salesb.core.ui.SaleItemDetailProduct

@Composable
fun SaleListScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: SaleListViewModel = hiltViewModel(),
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val sales = viewModel.pagedProducts.collectAsLazyPagingItems()
    val error = (sales.loadState.refresh as? LoadState.Error)?.error?.message
    val parameter by viewModel.parameter.collectAsState()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var saleGetValue by remember { mutableStateOf<Sale?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 10.dp, end = 12.dp, top = 6.dp)
    ) {
        // if get error when fetching products
        if (error != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorScreen(
                    error = error,
                    modifier = Modifier
                        .wrapContentWidth()
                        .padding(bottom = 16.dp)
                )
                DefaultButton(
                    modifier = Modifier.wrapContentWidth(),
                    text = stringResource(R.string.retry)
                ) {
                    sales.refresh()
                }
            }
        } else {
            // Show Progress bar waiting load products
            if (!isSearching && sales.itemCount == 0) {
                AppLoadingScreen(text = stringResource(R.string.loading_sales))
            } else {
                Column {
                    // Head
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TitleLarge(
                            title = stringResource(R.string.my_sales),
                            modifier = Modifier.padding(top = 22.dp)
                        )

                        Row {
                            // Barre de recherche
                            AppTextField(
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
                                placeholder = stringResource(R.string.search_sale_place_holder),
                                fieldType = FieldType.Text,
                                fieldColor = Silver,
                                modifier = Modifier.padding(top = 8.dp),
                                shape = RoundedCornerShape(26.dp)
                            )

                            DatePickerFieldToModal(
                                modifier = Modifier
                                    .width(230.dp)
                                    .padding(start = 20.dp),
                                label = stringResource(R.string.start_date),
                                defaultDate = currentDateString(pattern = "dd/MM/yyyy")
                            ) { dateValue ->
                                viewModel.updateStartDateQuery(dateValue)
                            }

                            DatePickerFieldToModal(
                                modifier = Modifier
                                    .width(230.dp)
                                    .padding(horizontal = 20.dp),
                                label = stringResource(R.string.end_date),
                                defaultDate = currentDateString(pattern = "dd/MM/yyyy")
                            ) { dateValue ->
                                viewModel.updateEndDateQuery(dateValue)
                            }

                            DefaultButton(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .padding(top = 10.dp),
                                text = stringResource(R.string.view_chart)
                            ) {

                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(top = 26.dp))

                    // Paginated list
                    SaleCardList(
                        sales = sales,
                        isSearching = isSearching,
                        onViewDetail = { sale ->
                            showDialog = true
                            saleGetValue = sale
                        },
                        onDelete = { id, libelle ->
                            /* showDialog = true
                             productIdLibelle = Pair(id, libelle)*/
                        },
                    )

                    if (showDialog) {
                        AppCustomDialog(setShowDialog = { showDialog = it} ) {
                            SaleItemDetailProduct(sale = saleGetValue, devise = parameter.devise)
                        }
                    }
                }
            }
        }
    }
}
