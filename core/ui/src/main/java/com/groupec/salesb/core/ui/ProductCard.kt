package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Product
import com.groupec.salesb.core.toDate

@Composable
fun ProductCard(
    product: Product,
    isSelected: Boolean,
    onViewDetail: (Product) -> Unit,
    onDelete: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cardColor = if (isSelected) Silver else White // Define the color based on the 'selected' state

    Card(
        shape = RoundedCornerShape(0.dp),
        // onClick = {}
    ) {
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
                    text = stringResource(
                        R.string.product_item_detail,
                        product.datecreation?.dayMonthYear() ?: stringResource(R.string.none),
                        product.prixttc, product.qtestock ?: 0,
                        product.username ?: ""
                    )
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
    }
}

@Composable
fun HorizontalDotsIconButton(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        //modifier = Modifier.size(40.dp) // Set the button size
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                // .size(36.dp) // Inner box for the circular background
                .background(Color.Unspecified, shape = CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        ) {
            Icon(
                imageVector = AppIcons.MoreHoriz, // Horizontal three dots icon
                contentDescription = "More options",
                // tint = Color.Gray, // Color of the dots
                modifier = Modifier.padding(2.dp) // Adjust the size of the icon inside the circle
            )
        }
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
            onViewDetail = {},
            onDelete = { id, libelle ->
            }
        )
    }
}
