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
import com.groupec.salesb.core.model.data.User

@Composable
fun UserCard(
    user: User,
    isSelected: Boolean,
    onViewDetail: (User) -> Unit,
    onDelete: (Int, String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val cardColor = if (isSelected) Silver else White // Define the color based on the 'selected' state

    val userActive= if (user.actif) {
        stringResource(R.string.active)
    } else {
        stringResource(R.string.disable)
    }
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = cardColor
        ),
        headlineContent = {
            Text(
                text = user.nomprenom,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis // Ajoute "..." si le texte est trop long
            )
        },
        supportingContent = {
            Text(
                text = stringResource(
                    R.string.user_item_detail,
                    user.datecreation?.dayMonthYear() ?: stringResource(R.string.none),
                    user.email, user.tel ?: stringResource(R.string.none), userActive
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
                        onViewDetail(user)
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
                        onDelete(user.id ?: 0, user.nomprenom)
                        expanded = false // Close DropdownMenuItem
                    }
                )
            }

        }
    )
    HorizontalDivider()
}


@Preview
// @Preview(device = Devices.TABLET)
@Composable
fun UserCardPreview() {
    SalesBAppTheme {
        UserCard(
            User(
                id = 1,
                nomprenom = "SATRE Paul",
                email = "paul@gmail.com",
                password = "123",
                adresse = "1",
                tel = "1",
                privilege = "1,2,3",
                actif = true,
                firstlogin = false,
                synchronised = true
            ),
            isSelected = false,
            onViewDetail = {},
            onDelete = { id, libelle ->
            }
        )
    }
}
