package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.allowOnlyDigits
import com.groupec.salesb.core.designsystem.R
import com.groupec.salesb.core.designsystem.component.AppEditableExposedDropdown
import com.groupec.salesb.core.designsystem.component.AppTextField
import com.groupec.salesb.core.designsystem.component.DefaultButton
import com.groupec.salesb.core.designsystem.component.FieldType
import com.groupec.salesb.core.designsystem.component.IconTextButton
import com.groupec.salesb.core.designsystem.component.KeyboardAction
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.normalizeDecimalSeparator

@Composable
fun ProductForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    categorieItems: List<Pair<String, String>>,
    rayonItems: List<Pair<String, String>>,
    fournisseurItems: List<Pair<String, String>>,
    products: ProductDataForm,
    categorielibelleState: TextFieldValue,  // Use TextField to better handle onchange on Spinner
    navigateToCategory: () -> Unit,
    navigateToRayon: () -> Unit,
    rayonlibelleState: TextFieldValue,
    fournisseurlibelleState: TextFieldValue,
    onProductDataChanged: (ProductDataForm) -> Unit, // We send it parent side to make operation like ResetForm
    onCategorielibelleState: (TextFieldValue) -> Unit,  // Use TextField to better handle onchange on Spinner
    onRayonlibelleState: (TextFieldValue) -> Unit,
    onFournisseurlibelleState: (TextFieldValue) -> Unit,
    onSubmitForm: (product: ProductDataForm) -> Unit
) {

    var isLibelleError by remember { mutableStateOf(false) }
    var isPrixttcError by remember { mutableStateOf(false) }
    var isCategorieLibelleError by remember { mutableStateOf(false) }
    var isRayonLibelleError by remember { mutableStateOf(false) }
    var isFournisseurLibelleError by remember { mutableStateOf(false) }

    val submitAction = {
        isLibelleError = products.libelle.isEmpty()
        isPrixttcError = products.prixttc.isEmpty()
        isCategorieLibelleError = products.categorielibelle.isNotEmpty() && categorieItems.none { it.second == products.categorielibelle }
        isRayonLibelleError = products.rayonlibelle.isNotEmpty() && rayonItems.none { it.second == products.rayonlibelle }
        isFournisseurLibelleError =  products.fournisseurlibelle.isNotEmpty() && fournisseurItems.none { it.second == products.fournisseurlibelle }
        if (!isLibelleError && !isPrixttcError && !isCategorieLibelleError
            && !isRayonLibelleError && !isFournisseurLibelleError) {
            // Submit the form
            onSubmitForm(products)
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        AppTextField(
            value = products.reference,
            onChange = { data ->
                onProductDataChanged(products.copy(reference = data))
            },
            label = stringResource(id = R.string.label_ref),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_ref)
            ),
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = products.libelle,
            onChange = { data ->
                onProductDataChanged(products.copy(libelle = data))
                if (isLibelleError) isLibelleError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_libelle),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_libelle)
            ),
            isError = isLibelleError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = products.description,
            onChange = { data ->
                onProductDataChanged(products.copy(description = data))
            },
            label = stringResource(id = R.string.label_desc),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_desc)
            ),
            fieldColor = White,
            maxLines = 5,
            singleLine = false,
            keyboardAction = KeyboardAction.Unspecified,
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        )

        AppTextField(
            value = products.prixttc,
            onChange = { data ->
                onProductDataChanged(products.copy(prixttc = data.normalizeDecimalSeparator()))
                if (isPrixttcError) isPrixttcError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_price),
            placeholder = "0.0",
            fieldType = FieldType.Number,
            isError = isPrixttcError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
                value = products.qtestock,
                onChange = { data ->
                    onProductDataChanged(products.copy(qtestock = data.allowOnlyDigits()))
                },
                label = stringResource(id = R.string.label_qte_stock),
                placeholder = stringResource(
                    R.string.enter_your_value,
                    stringResource(R.string.label_qte_stock)
                ),
                fieldType = FieldType.Number,
                fieldColor = White,
                modifier = Modifier.fillMaxWidth()
            )

            AppTextField(
                value = products.stockmini,
                onChange = { data ->
                    onProductDataChanged(products.copy(stockmini = data.allowOnlyDigits()))
                },
                label = stringResource(id = R.string.label_stock_mini),
                placeholder = stringResource(
                    R.string.enter_your_value,
                    stringResource(R.string.label_stock_mini)
                ),
                fieldType = FieldType.Number,
                fieldColor = White,
                modifier = Modifier.fillMaxWidth()
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            AppEditableExposedDropdown(
                items = categorieItems,
                label = stringResource(id = R.string.label_categorie),
                isError = isCategorieLibelleError,
                supportingText = if (
                    products.categorielibelle.isNotEmpty() && categorieItems.none { it.second == products.categorielibelle }
                ) {
                    {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.invalid_select),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    null
                },
                value = categorielibelleState,
                onValueChange = {
                    onCategorielibelleState(it)
                    if (isCategorieLibelleError) isCategorieLibelleError = false // Supprimer l'erreur
                },
                modifier = Modifier.weight(1f),
            ) { item ->
                onProductDataChanged(products.copy(categorieid = item.first, categorielibelle = item.second))
                if (isCategorieLibelleError) isCategorieLibelleError = false // Supprimer l'erreur
            }

            IconTextButton(
                modifier = Modifier.padding(top = 4.dp, start = 12.dp),
                icon = {
                    Icon(
                        imageVector = AppIcons.Add,
                        contentDescription = "Add more categories",
                        // modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Silver, contentColor = Color.Black)
            ) {
                navigateToCategory()
            }

        }

        /*
            Row(modifier = Modifier.fillMaxWidth()) {
                AppEditableExposedDropdown(
                    items = rayonItems,
                    modifier = Modifier.weight(1f),
                    label = stringResource(id = R.string.label_rayon),
                    isError = isRayonLibelleError,
                    supportingText = if (
                        products.rayonlibelle.isNotEmpty() && rayonItems.none { it.second == products.rayonlibelle }
                    ) {
                        {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(R.string.invalid_select),
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else {
                        null
                    },
                    value = rayonlibelleState,
                    onValueChange = {
                       onRayonlibelleState(it)
                        if (isRayonLibelleError) isRayonLibelleError = false // Supprimer l'erreur
                    },
                    onItemSelected = { item ->
                        onProductDataChanged(products.copy(rayonid = item.first, rayonlibelle = item.second))
                        if (isRayonLibelleError) isRayonLibelleError = false // Supprimer l'erreur
                    }
                )

                IconTextButton(
                    modifier = Modifier.padding(top = 4.dp, start = 12.dp),
                    icon = {
                        Icon(
                            imageVector = AppIcons.Add,
                            contentDescription = "Add more products",
                            // modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Silver, contentColor = Color.Black)
                ) {
                    navigateToRayon()
                }

            }
        */

       /* Row(modifier = Modifier.fillMaxWidth()) {
            AppEditableExposedDropdown(
                items = fournisseurItems,
                modifier = Modifier.weight(1f),
                label = stringResource(id = R.string.label_fournisseur),
                isError = isFournisseurLibelleError,
                supportingText = if (
                    products.fournisseurlibelle.isNotEmpty() && fournisseurItems.none { it.second == products.fournisseurlibelle }
                ) {
                    {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(R.string.invalid_select),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                } else {
                    null
                },
                value = fournisseurlibelleState,
                onValueChange = {
                    onFournisseurlibelleState(it)
                    if (isFournisseurLibelleError) isFournisseurLibelleError = false // Supprimer l'erreur
                },
                onItemSelected = { item ->
                    onProductDataChanged(products.copy(fournisseurid = item.first, fournisseurlibelle = item.second))
                    if (isFournisseurLibelleError) isFournisseurLibelleError = false // Supprimer l'erreur
                }
            )

            IconTextButton(
                modifier = Modifier.padding(top = 4.dp, start = 12.dp),
                icon = {
                    Icon(
                        imageVector = AppIcons.Add,
                        contentDescription = "Add more products",
                        // modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = Silver, contentColor = Color.Black)
            ) {

            }

        }*/

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            DefaultButton(
                onClick = submitAction,
                text = stringResource(id = R.string.btn_save),
                isLoading = isLoading
            )
        }
    }
}

data class ProductDataForm(
    val id: String = "",
    val reference: String = "",
    val libelle: String = "",
    val description: String = "",
    val image: String = "",
    val prixttc: String = "0",
    val qtestock: String = "",
    val stockmini: String = "",
    val categorieid: String = "",
    val categorielibelle: String = "",
    val rayonid: String = "",
    val rayonlibelle: String = "",
    val fournisseurid: String = "",
    val fournisseurlibelle: String = ""
)
