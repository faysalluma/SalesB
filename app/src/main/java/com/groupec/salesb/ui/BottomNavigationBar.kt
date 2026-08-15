package com.groupec.salesb.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.navigation.NavigationIcon
import com.groupec.salesb.navigation.NavigationItem


@Composable
fun BottomNavigationBar(
    items: List<NavigationItem>,
    currentRoute: String?,
    isServiceBusiness: Boolean,
    onItemClick: (NavigationItem) -> Unit
) {
    NavigationBar(
        modifier = Modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { navigationItem ->
            val itemLabel = if (navigationItem == NavigationItem.Product) {
                LocalContext.current.getCatalogItemLabel(
                    isServiceBusiness = isServiceBusiness,
                    plural = true,
                    capitalize = true
                )
            } else {
                stringResource(id = navigationItem.title)
            }
            NavigationBarItem(
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                ),
                selected = currentRoute == navigationItem.route,
                onClick = { onItemClick(navigationItem) },
                icon = {
                    when (navigationItem.icon) {
                        is NavigationIcon.VectorIcon -> {
                            Icon(
                                imageVector = navigationItem.icon.imageVector,
                                contentDescription = itemLabel,
                                //modifier = Modifier.size(32.dp)
                            )
                        }
                        is NavigationIcon.DrawableIcon -> {
                            Icon(
                                painter = painterResource(navigationItem.icon.drawableRes),
                                contentDescription = itemLabel,
                                // modifier = Modifier.size(30.dp)
                            )
                        }
                        null -> {
                            // No icon
                        }
                    }
                },
                label = {
                    Text(
                        text = itemLabel,
                        style = if (navigationItem.route == currentRoute) MaterialTheme.typography.labelLarge
                        else MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        color = if (navigationItem.route == currentRoute) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Unspecified
                        },
                        overflow = TextOverflow.Ellipsis
                    )
                }
            )
        }
    }
}
