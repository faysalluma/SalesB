package com.groupec.feature.productdetail

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.groupec.salesb.core.FeatureAccess
import com.groupec.salesb.core.FormUIState
import com.groupec.salesb.core.Privileges
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.designsystem.component.AppHeadLine
import com.groupec.salesb.core.designsystem.component.SnackbarVisualsWithState
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.ui.AddImage
import com.groupec.salesb.core.ui.ComposableLifecycle
import com.groupec.salesb.core.ui.ProFeatureBottomSheet
import com.groupec.salesb.core.ui.ProductDataForm
import com.groupec.salesb.core.ui.ProductForm
import java.io.File

@Composable
fun ProductDetailScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    product: Product?,
    refreshProducts: (() -> Unit) ? = null,
    removeSelectedBgColor: (() -> Unit) ? = null,
    navigateToHome: (() -> Unit)? = null,
    onPopBack: (() -> Unit)? = null,
    isExpandedWidth: Boolean,
    viewModel: ProductDetailViewModel = hiltViewModel(),
    navigateToCategory: () -> Unit,
    navigateToRayon: () -> Unit,
    onNavigateToSubscription: () -> Unit,
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val addProductState by viewModel.addProductUiState.collectAsState()
    val isLoading = addProductState is FormUIState.Loading
    val categoriesPairState by viewModel.categoriesUiPairState.collectAsState()
    val rayonsPairState by viewModel.rayonsUiPairState.collectAsState()
    val parameterState by viewModel.parameterState.collectAsState()
    val totalProductsCount by viewModel.totalProductsCountState.collectAsState()
    val catalogLabelSingular = context.getCatalogItemLabel(
        isServiceView = parameterState.serviceview,
        plural = false
    )

    var productDataForm by remember { mutableStateOf(ProductDataForm()) }
    var categorielibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.categorielibelle)) }
    var rayonlibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.rayonlibelle)) }
    var fournisseurlibelleState by remember { mutableStateOf(TextFieldValue(productDataForm.fournisseurlibelle)) }
    val uri = remember { mutableStateOf<Uri?>(null) }

    val userStoreState by viewModel.userStoreState.collectAsState()
    val privileges = userStoreState.getPrivileges()
    var showProBottomSheet by remember { mutableStateOf(false) }
    var proBottomSheetTitle by rememberSaveable { mutableStateOf("") }
    val productLimitTitle = stringResource(com.groupec.salesb.core.ui.R.string.pro_feature_products_limit_title)

    ComposableLifecycle(
        onResume = {
            viewModel.getParameter()
            viewModel.getCategories()
            viewModel.getRayons()
            viewModel.refreshTotalProductsCount()
        }
    )

    // When on detail and popBackStack : avoid show toast message
    BackHandler {
        onPopBack?.invoke()
    }

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
                if (isExpandedWidth) {
                    removeSelectedBgColor?.invoke()
                    refreshProducts?.invoke() // Notify list to refresh
                    snackbarHostState.showSnackbar(
                        SnackbarVisualsWithState(
                            message = context.getString(com.groupec.salesb.core.ui.R.string.product_operate_succesfully)
                        )
                    )
                    viewModel.resetFlow()
                } else {
                    navigateToHome?.invoke()
                }
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

    if (showProBottomSheet) {
        ProFeatureBottomSheet(
            title = proBottomSheetTitle,
            onDismiss = {
                focusManager.clearFocus()
                showProBottomSheet = false
            },
            onUpgradeClick = {
                showProBottomSheet = false
                onNavigateToSubscription()
            }
        )
    }

    Column {
        AppHeadLine(
            modifier = Modifier.padding(bottom = 28.dp),
            text = stringResource(R.string.detail_title, catalogLabelSingular),
            trailingContent = {
                Text(
                    stringResource(com.groupec.salesb.core.ui.R.string.btn_cancel),
                    color = Primary,
                    modifier = Modifier.clickable {
                        resetProductForm()
                        removeSelectedBgColor?.invoke()
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
                isServiceView = parameterState.serviceview,
                products = productDataForm,
                categorielibelleState = categorielibelleState,
                navigateToCategory = {
                    navigateToCategory()
                },
                navigateToRayon = {
                    navigateToRayon()
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
                    val isCreatingProduct = productDataForm.id.isBlank()
                    if (
                        isCreatingProduct &&
                        !FeatureAccess.canCreateProduct(
                            isProActive = userStoreState.isProActive,
                            totalProductsCount = totalProductsCount,
                        )
                    ) {
                        proBottomSheetTitle = productLimitTitle
                        showProBottomSheet = true
                    } else {
                        viewModel.addProduct(product, uri.value)
                    }
                }
            )
        }
    }
}
