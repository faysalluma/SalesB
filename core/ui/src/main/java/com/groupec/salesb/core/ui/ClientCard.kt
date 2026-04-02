package com.groupec.salesb.core.ui

import androidx.compose.foundation.background
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.groupec.salesb.core.dayMonthYear
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.model.data.Client

@Composable
fun ClientCard(
    client: Client,
    isSelected: Boolean,
    onViewDetail: (Client) -> Unit,
    onDelete: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cardColor = if (isSelected) Silver else White

    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = cardColor
        ),
        headlineContent = {
            Text(
                text = client.nomprenom,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            Text(
                text = stringResource(
                    R.string.client_item_detail,
                    client.datemodif?.dayMonthYear() ?: stringResource(R.string.none),
                    client.nomprenom,
                    client.telephone ?: stringResource(R.string.none),
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
                        onViewDetail(client)
                        expanded = false
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
                        onDelete(client.id ?: 0, client.nomprenom)
                        expanded = false
                    }
                )
            }
        }
    )
    HorizontalDivider()
}

@Preview
@Composable
fun ClientCardPreview() {
    SalesBAppTheme {
        ClientCard(
            client = Client(
                id = 1,
                nomprenom = "Jean Dupont",
                adresse = "Paris",
                telephone = "+33 6 00 00 00 00"
            ),
            isSelected = false,
            onViewDetail = {},
            onDelete = { _, _ -> }
        )
    }
}
