package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
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
import androidx.compose.ui.text.style.TextAlign
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
import com.groupec.salesb.core.model.data.Sale
import com.groupec.salesb.core.model.data.SaleDetail
import com.groupec.salesb.core.toDate
import com.groupec.salesb.core.toDateString

@Composable
fun SaleCard(
    sale: Sale,
    onViewDetail: (Sale) -> Unit,
    onDelete: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

        TableRow(
            sale = sale,
            actionItem = {
                // DropDown Menu
                 Box {
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
                                    imageVector = AppIcons.MoreInfo,
                                    contentDescription = "More Info Icon"
                                )
                            },
                            text = { Text(stringResource(R.string.view_detail), color = Black) },
                            onClick = {
                                onViewDetail(sale)
                                expanded = false // Close DropdownMenuItem
                            }
                        )
                        /*DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = AppIcons.Delete,
                                    contentDescription = "Delete Icon"
                                )
                            },
                            text = { Text(stringResource(R.string.delete_item), color = Black) },
                            onClick = {
                                onDelete(sale.id?:0, sale.id.toString())
                                expanded = false // Close DropdownMenuItem
                            }
                        )*/
                    }
                }
            }
        )
}

@Composable
fun SaleHeaderCard() {
    TableRow(isTitle = true)
}

@Composable
fun TableRow(
    sale: Sale ? = null,
    isTitle: Boolean = false,
    actionItem: @Composable (() -> Unit) ? = null)
{

    val column1Weight = .15f
    val column2Weight = .3f
    val column3Weight = .25f
    val column4Weight = .3f

    Row(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.weight(1f)) {
            TableCell(
                text = sale?.id?.toString() ?: "Id",
                weight = column1Weight,
                alignment = TextAlign.Left,
                isTitle = isTitle
            )
            TableCell(
                text =  sale?.datevente?.toDateString(
                    format = "dd/MM/yyyy  HH:mm:ss"
                ) ?: "Date" ,
                weight = column2Weight,
                isTitle = isTitle
            )
            TableCell(
                text =  sale?.totalprix?.toString() ?: stringResource(R.string.total_amount),
                weight = column3Weight,
                isTitle = isTitle
            )
            TableCell(
                text =  sale?.username ?: stringResource(R.string.registered_by),
                weight = column4Weight,
                isTitle = isTitle
            )
        }
        if (isTitle) {
            TableCell(
                text =  sale?.username ?: stringResource(R.string.actions),
                alignment = TextAlign.Right,
                isTitle = isTitle
            )
        } else {
            actionItem?.invoke()
        }

    }
    HorizontalDivider(color = Color.LightGray)
}

@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun SaleCardPreview() {
    SalesBAppTheme {
        Column {
            SaleHeaderCard()
            SaleCard(
                Sale(
                    id = 1,
                    totalprix = 3.0,
                    details = listOf(SaleDetail(1,"P1", 2.0, 1.0))
                ),
                onViewDetail = {},
                onDelete = { id, libelle ->
                }
            )
        }

    }
}
