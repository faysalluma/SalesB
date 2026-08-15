package com.groupec.salesb.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.groupec.salesb.core.designsystem.theme.Primary
import com.groupec.salesb.core.designsystem.theme.Silver
import com.groupec.salesb.core.designsystem.theme.White
import com.groupec.salesb.core.getCatalogItemLabel
import com.groupec.salesb.navigation.NavigationIcon
import com.groupec.salesb.navigation.NavigationItem

@Composable
fun MyNavigationRail(
    items: List<NavigationItem>,
    navController: NavController,
    isServiceBusiness: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Observer la destination actuelle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: items.firstOrNull()?.route

    NavigationRail(
        containerColor = Silver,

        modifier = modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
    ) {
        items.forEach { item ->
            val itemLabel = if (item == NavigationItem.Product) {
                context.getCatalogItemLabel(
                    isServiceBusiness = isServiceBusiness,
                    plural = true,
                    capitalize = true
                )
            } else {
                stringResource(id = item.title)
            }
            NavigationRailItem(
                icon = {
                    when (item.icon) {
                        is NavigationIcon.VectorIcon -> {
                            Icon(
                                imageVector = item.icon.imageVector,
                                contentDescription = itemLabel,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        is NavigationIcon.DrawableIcon -> {
                            Icon(
                                painter = painterResource(item.icon.drawableRes),
                                contentDescription = itemLabel,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        null -> {
                            // No icon
                        }
                    }
                },
                label = { Text(
                    itemLabel, fontSize = 16.sp,
                    textAlign = TextAlign.Center
                    )
                },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        // currentRoute = item.route
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) // Supprime la pile jusqu'à startDestinationId
                            launchSingleTop = true // Eviter les doublons
                        }
                    }
                },
                colors = NavigationRailItemDefaults.colors(
                    selectedIconColor = White,
                    selectedTextColor = Primary,
                    indicatorColor = Primary
                )
            )
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Preview
@Composable
fun MyNavigationRailPreview() {
    MyNavigationRail(
        items = listOf(
            NavigationItem.Home,
            NavigationItem.SaveSale,
            NavigationItem.MySales,
            NavigationItem.Product,
            NavigationItem.Outputs
        ),
        navController = rememberNavController(),
        isServiceBusiness = false,
    )
}
