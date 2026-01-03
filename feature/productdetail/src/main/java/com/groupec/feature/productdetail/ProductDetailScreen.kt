package com.groupec.feature.productdetail

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groupec.salesb.core.Approval
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.AddImage
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.ProductDataForm
import com.groupec.salesb.core.ui.ProductForm
import java.io.File

@Composable
fun ProductDetailScreen(
    snackbarHostState: SnackbarHostState,
    product: Product?,
    navigateToCategory: () -> Unit,
    navigateToRayon: () -> Unit,
    refreshProducts: () -> Unit,
    removeSelectedBgColor: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductDetailViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addProductState by viewModel.addProductUiState.collectAsState()
    val isLoading = addProductState is FormUIState.Loading
    val categoriesPairState by viewModel.categoriesUiPairState.collectAsState()
    val rayonsPairState by viewModel.rayonsUiPairState.collectAsState()

    var productDataForm by remember { mutableStateOf(ProductDataForm()) }
    var categorielibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.categorielibelle)) }
    var rayonlibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.rayonlibelle)) }
    var fournisseurlibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.fournisseurlibelle)) }
    val uri = remember { mutableStateOf<Uri?>(null) }

    val userStoreState by viewModel.userStoreState.collectAsState()
    val privileges = userStoreState.getPrivileges()

    ComposableLifecycle(
        onResume = {
            viewModel.getCategories()
            viewModel.getRayons()
        }
    )

    LaunchedEffect(product) {
        // Form Data and methods
       product?.let {
            uri.value = if (it.image != null) {
                Uri.parse(com.groupec.salesb.core.Constants.UPLOAD_URL.plus(it.image))
            } else {
                null
            }

            productDataForm = ProductDataForm(
                id = it.id.toString(),
                reference = it.reference.orEmpty(),
                libelle = it.libelle,
                description = it.description.orEmpty(),
                prixttc = it.prixttc.toString(),
                qtestock = it.qtestock?.toString().orEmpty(),
                stockmini = it.stockmini?.toString().orEmpty(),
                categorieid = it.categorieid?.toString().orEmpty(),
                categorielibelle = it.categorielibelle.orEmpty(),
                rayonid = it.rayonid?.toString().orEmpty(),
                rayonlibelle = it.rayonlibelle.orEmpty(),
                fournisseurid = it.fournisseurid?.toString().orEmpty(),
                fournisseurlibelle = it.fournisseurlibelle.orEmpty(),
                image = it.image.orEmpty()
            )
           categorielibelleState = TextFieldValue(it.categorielibelle.orEmpty())
           rayonlibelleState = TextFieldValue(it.rayonlibelle.orEmpty())
           fournisseurlibelleState = TextFieldValue(it.fournisseurlibelle.orEmpty())
           // Reset focus on form
           focusManager.clearFocus()
        }
    }

    val resetProductForm = {
        uri.value?.lastPathSegment?.let {  viewModel.deleteImageFromCache(context, it) }
        uri.value = null
        productDataForm = ProductDataForm()
        categorielibelleState = TextFieldValue("")
        rayonlibelleState = TextFieldValue("")
        fournisseurlibelleState = TextFieldValue("")
        // Reset focus on form
        focusManager.clearFocus()
    }

    when (addProductState) {
        is FormUIState.Success -> {
            LaunchedEffect(Unit) {
                resetProductForm()
                removeSelectedBgColor()
                refreshProducts() // Notify list to refresh
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
                        message =(addProductState as FormUIState.Error).message,
                        isError = true
                    )
                )
                viewModel.resetFlow()
            }
        }

        else -> {}
    }

    Column {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 28.dp),
            text = stringResource(R.string.detail_title),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetProductForm()
                        removeSelectedBgColor()
                    }
                )

            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //image to show bottom sheet
            AddImage(
                directory = File(context.cacheDir, "images"),
                uri = uri.value,
                onSetUri = {
                    uri.value = it
                    if (it == null) productDataForm = productDataForm.copy(image = "") // Notify image delete for external api
                },
                /*  upload = {
                      viewModel.uploadImage(it)
                  },*/
                deleteFile = { filename ->
                    viewModel.deleteImageFromCache(context, filename)
                }
            )

            ProductForm(
                modifier = Modifier.fillMaxWidth(0.8f),
                isLoading = isLoading,
                categorieItems = categoriesPairState,
                rayonItems = rayonsPairState,
                fournisseurItems = listOf(),
                products = productDataForm,
                categorielibelleState = categorielibelleState,
                navigateToCategory = {
                    if (privileges.any {
                        it in Privileges.Category.getKeysByApprovals(
                            listOf(
                                Approval.AUTHORIZE_ADD,
                                Approval.AUTHORIZE_EDIT,
                                Approval.AUTHORIZE_DELETE,
                            )
                        )
                    }) {
                        navigateToCategory()
                    } else {
                        Toast.makeText(context, context.getString(com.groupec.salesb.core.R.string.no_visual_allowed),
                            Toast.LENGTH_SHORT).show()
                    }
                },
                navigateToRayon = {
                    if (privileges.any {
                            it in Privileges.Rayon.getKeysByApprovals(
                                listOf(
                                    Approval.AUTHORIZE_ADD,
                                    Approval.AUTHORIZE_EDIT,
                                    Approval.AUTHORIZE_DELETE,
                                )
                            )
                        }) {
                        navigateToRayon()
                    } else {
                        Toast.makeText(context, context.getString(com.groupec.salesb.core.R.string.no_visual_allowed),
                            Toast.LENGTH_SHORT).show()
                    }
                },
                rayonlibelleState = rayonlibelleState,
                fournisseurlibelleState = fournisseurlibelleState,
                onProductDataChanged = { newProduct ->
                    productDataForm = newProduct
                },
                onCategorielibelleState = { newCategorie ->
                    categorielibelleState = newCategorie
                },
                onRayonlibelleState = { newRayon ->
                    rayonlibelleState = newRayon
                },
                onFournisseurlibelleState = { newFournisseur ->
                    fournisseurlibelleState = newFournisseur
                },
                onSubmitForm = { product ->
                    viewModel.addProduct(product, uri.value)
                }
            )
        }
    }
}