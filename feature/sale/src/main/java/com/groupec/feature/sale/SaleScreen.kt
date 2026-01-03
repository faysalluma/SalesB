package com.groupec.feature.sale

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.print.PrintAction
import com.groupec.salesb.core.designsystem.component.AppLoadingScreen
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.EmptyScreen
import com.groupec.salesb.core.designsystem.component.ErrorScreen
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.component.TitleLarge
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.getDrawableResIdIfExists
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.print.Print
import com.groupec.salesb.core.ui.ProductGridAdaptive
import com.groupec.salesb.core.ui.SaleDetailCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SaleScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    viewModel: SaleViewModel = hiltViewModel(),
    navigateToProduct: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val products = viewModel.pagedProducts.collectAsLazyPagingItems()
    val error = (products.loadState.refresh as? LoadState.Error)?.error?.message
    val parameter by viewModel.parameter.collectAsState()
    val addSaleUiState by viewModel.addSaleUiState.collectAsState()
    val isLoading = addSaleUiState is FormUIState.Loading

    // For selected Products and handling of multiples textfield created
    val selectedProducts = remember { mutableStateListOf<Pair<Int, Product>>() }
    val textFieldValues = remember { mutableStateMapOf<Int, String>() }
    val quantityCheck = remember { mutableStateMapOf<Int, Boolean>() }

    // Bluetooth
    val bluetoothPrint = Print(context)
    var savedSale by remember { mutableStateOf<Sale?>(null) }

    val bluetoothPermissions =
        // Checks if the device has Android 12 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                )
            )
        } else {
            rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADMIN,
                )
            )
        }

    val enableBluetoothContract = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            Log.d("bluetoothLauncher", "Success")
            savedSale?.let { sale ->
                scope.launch(Dispatchers.IO) {
                    bluetoothPrint.print(
                        getDrawableResIdIfExists(context),
                        sale = sale,
                        parameter = parameter
                    )
                }
            }
        } else {
            Log.w("bluetoothLauncher", "Failed")
        }
    }

    // This intent will open the enable bluetooth dialog
    val enableBluetoothIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
    val bluetoothManager = remember { context.getSystemService(BluetoothManager::class.java) }
    val bluetoothAdapter: BluetoothAdapter? = remember { bluetoothManager.adapter }

   when (addSaleUiState) {
       is FormUIState.Success -> {
           LaunchedEffect(Unit) {
               val (printAction, sale) = (addSaleUiState as FormUIState.Success).data
               savedSale = sale // Set saved sale
               when (printAction) {
                   PrintAction.Thermal -> {
                       if (bluetoothPermissions.allPermissionsGranted) {
                           if (bluetoothAdapter?.isEnabled == true) {
                               // Bluetooth is on print the receipt
                               scope.launch(Dispatchers.IO) {
                                   bluetoothPrint.print(
                                       getDrawableResIdIfExists(context),
                                       sale = sale,
                                       parameter = parameter
                                   )
                               }
                           } else {
                               // Bluetooth is off, ask user to turn it on
                               enableBluetoothContract.launch(enableBluetoothIntent)
                           }
                       } else {
                           bluetoothPermissions.launchMultiplePermissionRequest()
                           // Show error message
                           Toast.makeText(context,"Permission denied for access bluetooth", Toast.LENGTH_SHORT).show()
                       }
                   }
                   else -> {}
               }
               selectedProducts.clear()
               textFieldValues.clear()
               products.refresh()
               snackbarHostState.showSnackbar(
                   SnackbarVisualsWithState(
                       message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                   )
               )
               viewModel.resetFlow()
           }
       }
       is FormUIState.Error -> {
           LaunchedEffect(Unit) {
               snackbarHostState.showSnackbar(
                   SnackbarVisualsWithState(
                       message =(addSaleUiState as FormUIState.Error).message,
                       isError = true
                   )
               )
               viewModel.resetFlow()
           }
       }
       else -> {}
   }

   Row(modifier = Modifier.padding(start = 12.dp, top = 8.dp, end = 8.dp)) {
       Box (modifier = Modifier.weight(1.8f)) {
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
                       products.refresh()
                   }
               }
           } else {
               // Show Progress bar waiting load products
               if (!isSearching && products.itemCount == 0) {
                   AppLoadingScreen(text = stringResource(R.string.loading_products))
               } else {
                   Column {
                       // Head
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TitleLarge(
                                title = stringResource(R.string.my_products),
                                modifier = Modifier.padding(top = 12.dp)
                            )
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
                                placeholder = stringResource(R.string.search_product_place_holder),
                                fieldType = FieldType.Text,
                                fieldColor = Silver,
                                shape = RoundedCornerShape(28.dp)
                            )
                        }

                       HorizontalDivider(modifier = Modifier.padding(top = 12.dp))

                       // Paginated list
                       if (products.itemCount == 0) {
                           Box(
                               modifier = Modifier.fillMaxSize(),
                               contentAlignment = Alignment.Center
                           ) {
                               DefaultButton(
                                   modifier = Modifier.wrapContentWidth(),
                                   text = stringResource(R.string.add_product),
                                   onClick = navigateToProduct
                               )
                           }
                       } else {
                           ProductGridAdaptive(
                               products = products,
                               selectedProducts = selectedProducts,
                               textFieldValues = textFieldValues,
                               quantityCheck = quantityCheck,
                               isSearching = isSearching
                           )
                       }
                   }
               }
           }
       }

       Box (modifier = Modifier.weight(1.2f)) {
            SaleDetailScreen(
                selectedProducts = selectedProducts,
                textFieldValues = textFieldValues,
                quantityCheck = quantityCheck,
                devise = parameter.devise,
                isLoading = isLoading,
                onSave = { total, printAction ->
                    val saleDetail = selectedProducts.map { productLine ->
                        val quantity = textFieldValues[productLine.first]
                        SaleDetail(
                            id = productLine.second.id!!,
                            qte = quantity?.toDouble()?:0.0,
                            libelle = productLine.second.libelle,
                            prix = productLine.second.prixttc
                        )
                    }
                    viewModel.addSale(Sale(totalprix = total, details = saleDetail), printAction)
                },
                onClear = {
                    selectedProducts.clear()
                    textFieldValues.clear()
                }
            )
       }
   }
}

@Composable
fun SaleDetailScreen(
    selectedProducts: MutableList<Pair<Int, Product>>,
    textFieldValues: MutableMap<Int, String>,
    quantityCheck: MutableMap<Int, Boolean>,
    devise: String,
    isLoading: Boolean,
    onSave: (Double, PrintAction) -> Unit,
    onClear: () -> Unit
) {
    SaleDetailCard(
        selectedProducts = selectedProducts,
        textFieldValues = textFieldValues,
        devise = devise,
        isLoading = isLoading,
        onQuantityChange = { productLine ->
            val (index, product)  = productLine
            val quantityValue = textFieldValues[index]?.takeIf { it.isNotEmpty() }?.toDouble() ?: 1.0
            if (quantityValue <= 0) {
                selectedProducts.remove(productLine)
                textFieldValues.remove(index)
                quantityCheck.remove(index)
            } else {
                textFieldValues[index] = quantityValue.toString()
                // Check if quantityValue > product quantity
                quantityCheck[index] = product.qtestock?.let { quantityValue > it } ?: false
            }
        },
        onSave = onSave,
        onClear = onClear,
        quantityCheck = quantityCheck
    )
}
