package com.groupec.salesb.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.White


sealed class MenuItem {
    data class Action(val label: String, val onClick: () -> Unit) : MenuItem()
    data class SubMenu(val label: String, val children: List<Action>) : MenuItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTopAppBar(
    titleBar: String,
    onNavigationClick: (() -> Unit)? = null,
    dropDownItemsMenu: List<MenuItem> = emptyList()
) {
    var expanded by remember { mutableStateOf(false) }
    var subMenuExpandedIndex by remember { mutableStateOf<Int?>(null) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Primary,
            titleContentColor = White,
            navigationIconContentColor = White,
            actionIconContentColor = White
        ),
        title = {
            Text(text = titleBar.uppercase(), style = MaterialTheme.typography.titleMedium)
        },
        navigationIcon = {
            onNavigationClick?.let {
                IconButton(onClick = it) {
                    Icon(AppIcons.ArrowBack, contentDescription = "Back")
                }
            }
        },
        actions = {
            if (dropDownItemsMenu.isNotEmpty()) {
                IconButton(onClick = { expanded = true }) {
                    Icon(AppIcons.MoreVert, contentDescription = "Menu")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                        subMenuExpandedIndex = null
                    },
                    modifier = Modifier.background(White)
                ) {
                    dropDownItemsMenu.forEachIndexed { index, item ->
                        when (item) {
                            is MenuItem.Action -> DropdownMenuItem(
                                text = { Text(item.label, color = Black) },
                                onClick = {
                                    expanded = false
                                    item.onClick()
                                }
                            )

                            is MenuItem.SubMenu -> {
                                DropdownMenuItem(
                                    text = { Text(item.label, color = Black) },
                                    trailingIcon = {
                                        Icon(
                                            imageVector = AppIcons.ChevronRight,
                                            contentDescription = "SubMenu"
                                        )
                                    },
                                    onClick = {
                                        subMenuExpandedIndex =
                                            if (subMenuExpandedIndex == index) null else index
                                    }
                                )

                                if (subMenuExpandedIndex == index) {
                                    Column(
                                        modifier = Modifier
                                            .padding(start = 24.dp)
                                            .background(White)
                                    ) {
                                        item.children.forEach { action ->
                                            DropdownMenuItem(
                                                text = { Text(action.label, color = Black) },
                                                onClick = {
                                                    expanded = false
                                                    subMenuExpandedIndex = null
                                                    action.onClick()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}


@Preview("Top App Bar")
@Composable
private fun NiaTopAppBarPreview() {
    SalesBAppTheme {
        SampleTopAppBar(
            titleBar = "My top bar",
            onNavigationClick = {},
            dropDownItemsMenu = listOf(
                MenuItem.Action("Log out", {}),
                MenuItem.SubMenu(
                    "Parameters",
                    listOf(
                        MenuItem.Action("Category", {}),
                        MenuItem.Action("Rayon", {})
                    )
                )
            )
        )
    }
}
