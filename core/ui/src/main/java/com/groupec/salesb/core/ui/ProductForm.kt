package com.groupec.salesb.core.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
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

@Composable
fun ProductForm(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    categorieItems: List<Pair<String, String>>,
    onSubmitForm: (product: Product) -> Unit,
) {

    var products by remember { mutableStateOf(Product()) }
    var isLibelleError by remember { mutableStateOf(false) }
    var isPrixttcError by remember { mutableStateOf(false) }
    var isQteStockError by remember { mutableStateOf(false) }
    var isCategorieLibelleError by remember { mutableStateOf(false) }

    val submitAction = {
        isLibelleError = products.libelle.isEmpty()
        isPrixttcError = products.prixttc.isEmpty()
        isQteStockError = products.qtestock.isEmpty()
        isCategorieLibelleError = products.categorielibelle.isEmpty() || categorieItems.none { it.second == products.categorielibelle }
        if (!isLibelleError && !isPrixttcError && !isQteStockError && !isCategorieLibelleError) {
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
                products = products.copy(reference = data)
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
                products = products.copy(libelle = data)
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
                products = products.copy(description = data)
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
                products = products.copy(prixttc = data)
                if (isPrixttcError) isPrixttcError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_price),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_price)
            ),
            fieldType = FieldType.Number,
            isError = isPrixttcError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = products.qtestock,
            onChange = { data ->
                products = products.copy(qtestock = data)
                if (isQteStockError) isQteStockError = false //  Clear error when user starts typing
            },
            label = stringResource(id = R.string.label_qte_stock),
            placeholder = stringResource(
                R.string.enter_your_value,
                stringResource(R.string.label_qte_stock)
            ),
            fieldType = FieldType.Number,
            isError = isQteStockError,
            fieldColor = White,
            modifier = Modifier.fillMaxWidth()
        )

        AppTextField(
            value = products.stockmini,
            onChange = { data ->
                products = products.copy(stockmini = data)
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
                modifier = Modifier.weight(1f)
            ) { item ->
                products = products.copy(categorieid = item.first, categorielibelle = item.second)
                if (isCategorieLibelleError) isCategorieLibelleError = false // Supprimer l'erreur
            }

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

        }

        Row(modifier = Modifier.fillMaxWidth()) {
            AppEditableExposedDropdown(
                items = categorieItems,
                label = stringResource(id = R.string.label_rayon),
                modifier = Modifier.weight(1f)
            ) { item ->
                products = products.copy(rayonid = item.first, rayonlibelle = item.second)
            }

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

        }

        Row(modifier = Modifier.fillMaxWidth()) {
            AppEditableExposedDropdown(
                items = categorieItems,
                label = stringResource(id = R.string.label_fournisseur),
                modifier = Modifier.weight(1f)
            ) { item ->
                products = products.copy(categorieid = item.first, categorielibelle = item.second)
            }

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

        }


        /* AppTextField(
             value = credentials.password,
             leadingIcon = {
                 Icon(
                     imageVector = ImageVector.vectorResource(id = R.drawable.key),
                     contentDescription = null,
                     tint = Primary
                 )
             },
             onChange = { data ->
                 credentials = credentials.copy(password = data)
                 if (isPasswordError) isPasswordError = false //  Clear error when user starts typing
             },
             label = stringResource(id = R.string.label_password),
             placeholder = stringResource(id = R.string.enter_your_password),
             fieldType = FieldType.Password,
             isError = isPasswordError,
             keyboardAction = KeyboardAction.Done,
             submitAction = submitAction,
             modifier = Modifier.fillMaxWidth()
         )*/
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

data class Product(
    val id: String = "",
    val reference: String = "",
    val libelle: String = "",
    val description: String = "",
    val image: String = "",
    val prixttc: String = "0.0",
    val qtestock: String = "0",
    val stockmini: String = "",
    val categorieid: String = "",
    val categorielibelle: String = "",
    val rayonid: String = "",
    val rayonlibelle: String = "",
    val fournisseurid: String = "",
    val fournisseurlibelle: String = "",
    val datemodif: String = "",
    val userid: String = ""
)