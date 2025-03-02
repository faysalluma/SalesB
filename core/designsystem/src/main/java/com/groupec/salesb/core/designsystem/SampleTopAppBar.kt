package com.groupec.salesb.core.designsystem

import androidx.compose.foundation.background
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
import com.groupec.salesb.core.designsystem.icon.AppIcons
import com.groupec.salesb.core.designsystem.theme.Black
import com.groupec.salesb.core.designsystem.theme.SalesBAppTheme
import com.groupec.salesb.core.designsystem.theme.Green
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SampleTopAppBar(
    titleBar: String,
    onNavigationClick: (() -> Unit)? = null,
    dropDownItemsMenu: List<Pair<String, () -> Unit>> = emptyList()
) {
    var expanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Primary,
            titleContentColor = White,
            navigationIconContentColor = White,
            actionIconContentColor = White
        ),
        title = { Text(text = titleBar.uppercase(), style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            if (onNavigationClick != null) {
                IconButton(onClick = { onNavigationClick() }) {
                    Icon(
                        imageVector = AppIcons.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            if (dropDownItemsMenu.isNotEmpty()){
                IconButton(onClick = { expanded = true }) {
                    Icon(AppIcons.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(White)
                ) {
                    dropDownItemsMenu.forEach {item ->
                        DropdownMenuItem(
                            text = { Text(item.first, color = Black) },
                            onClick = {
                                expanded = false // Close DropdownMenuItem
                                item.second()
                            }
                        )
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
           dropDownItemsMenu = listOf(Pair("Settings", {}))
        )
    }
}