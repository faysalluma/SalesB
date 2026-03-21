package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.groupec.salesb.core.Constants
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.designsystem.component.AppCustomBottomSheet
import com.groupec.salesb.core.designsystem.component.ProductImage
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.Silver2
import com.groupec.salesb.core.designsystem.theme.Silver3
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.formatAmount
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.toDate

@Composable
fun ProductCard(
    product: Product,
    isSelected: Boolean,
    showQuantity: Boolean,
    isServiceView: Boolean,
    onViewDetail: (Product) -> Unit,
    onDelete: (Int, String) -> Unit
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var showDetailBottomSheet by remember { mutableStateOf(false) }
    val cardColor = if (isSelected) Silver else White // Define the color based on the 'selected' state
    val catalogLabelSingular = context.getCatalogItemLabel(
        isServiceView = isServiceView,
        plural = false
    )

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = cardColor
        ),
        headlineContent = {
            Text(
                text = product.libelle,
                fontWeight = FontWeight.Bold,
                //fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Ajoute "..." si le texte est trop long
            )
        },
        supportingContent = {
            Text(
                text = if (showQuantity) {
                    stringResource(
                        R.string.product_item_detail,
                        product.datecreation?.dayMonthYear() ?: stringResource(R.string.none),
                        product.prixttc,
                        product.qtestock ?: 0,
                        product.username ?: ""
                    )
                } else {
                    stringResource(
                        R.string.product_item_detail_without_quantity,
                        product.datecreation?.dayMonthYear() ?: stringResource(R.string.none),
                        product.prixttc,
                        product.username ?: ""
                    )
                }
            )
        },
        trailingContent = {
            IconButton(onClick = { expanded = true }) {
                Icon(AppIcons.MoreVert, contentDescription = "More options")
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(White)
            ) {
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.ViewDetail,
                            contentDescription = "Edit Icon"
                        )
                    },
                    text = {
                        Text(
                            stringResource(R.string.show_detail_item, catalogLabelSingular),
                            color = Black
                        )
                    },
                    onClick = {
                        showDetailBottomSheet = true
                        expanded = false // Close DropdownMenuItem
                    }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            painter = painterResource(AppIcons.Edit),
                            contentDescription = "Edit Icon"
                        )
                    },
                    text = { Text(stringResource(R.string.view_item), color = Black) },
                    onClick = {
                        onViewDetail(product)
                        expanded = false // Close DropdownMenuItem
                    }
                )
                DropdownMenuItem(
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.Delete,
                            contentDescription = "Delete Icon"
                        )
                    },
                    text = { Text(stringResource(R.string.delete_item), color = Black) },
                    onClick = {
                        onDelete(product.id?:0, product.libelle)
                        expanded = false // Close DropdownMenuItem
                    }
                )
            }
        }
    )
    HorizontalDivider()

    if (showDetailBottomSheet) {
        AppCustomBottomSheet(
            onDismiss = { showDetailBottomSheet = false }
        ) {
            ProductDetailBottomSheetContent(
                product = product,
                isServiceView = isServiceView,
                onClose = { showDetailBottomSheet = false }
            )
        }
    }
}

@Composable
private fun ProductDetailBottomSheetContent(
    product: Product,
    isServiceView: Boolean,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val none = stringResource(R.string.none)
    val catalogLabelSingular = context.getCatalogItemLabel(
        isServiceView = isServiceView,
        plural = false
    )
    val dateCreation = product.datecreation?.dayMonthYear() ?: none
    val dateLastUpdate = product.datemodif?.dayMonthYear() ?: none
    val priceTtc = product.prixttc.formatAmount()
    val category = product.categorielibelle?.takeIf { it.isNotBlank() }?.uppercase()
    val imageUrl = product.image?.takeIf { it.isNotBlank() }?.let { Constants.UPLOAD_URL.plus(it) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 8.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.product_detail_title, catalogLabelSingular),
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = AppIcons.Close,
                    contentDescription = stringResource(R.string.btn_close)
                )
            }
        }
        HorizontalDivider()

        if (imageUrl != null) {
            ProductImage(
                url = imageUrl,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                contentDescription = stringResource(
                    R.string.product_detail_image,
                    catalogLabelSingular
                ),
                isCircle = false,
                imageSize = 128.dp
            )
        }

        Text(
            text = product.libelle,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        category?.let {
            Text(
                text = it,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .background(Silver, RoundedCornerShape(50))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Primary,
                style = MaterialTheme.typography.labelLarge
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_id),
                value = product.id?.let { "#$it" } ?: none
            )
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_date_creation),
                value = dateCreation
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_reference),
                value = product.reference ?: none
            )
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_price_ttc),
                value = priceTtc
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = stringResource(R.string.product_detail_description).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = Silver3
            )
            Spacer(modifier = Modifier.size(6.dp))
            Text(
                text = product.description ?: none,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Normal
            )
        }

        if (!isServiceView) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProductDetailField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.product_detail_stock_quantity),
                    value = product.qtestock?.toString() ?: none
                )
                ProductDetailField(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.product_detail_stock_minimum),
                    value = product.stockmini?.toString() ?: none
                )
            }
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_category),
                value = product.categorielibelle ?: none
            )
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_rayon),
                value = product.rayonlibelle ?: none
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_date_last_update),
                value = dateLastUpdate
            )
            ProductDetailField(
                modifier = Modifier.weight(1f),
                label = stringResource(R.string.product_detail_created_by),
                value = product.username ?: none
            )
        }
    }
}

@Composable
private fun ProductDetailField(
    label: String,
    value: String,
    labelColor: Color = Silver3,
    modifier: Modifier = Modifier,
    valueColor: Color = Black,
    emphasizeValue: Boolean = false
) {
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = labelColor
        )
        Spacer(modifier = Modifier.size(2.dp))
        Text(
            text = value,
            style = if (emphasizeValue) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Normal,
            color = valueColor
        )
    }
}


@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun ProductCardPreview() {
    SalesBAppTheme {
        ProductCard(
            Product(
                id = 1,
                libelle = "Pain vienois",
                prixttc = 100.0,
                datemodif = "2022:10:12 16:51".toDate(),
            ),
            isSelected = false,
            showQuantity = true,
            isServiceView = false,
            onViewDetail = {},
            onDelete = { id, libelle ->
            }
        )
    }
}
