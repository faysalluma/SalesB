package com.groupec.salesb.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.groupec.salesb.navigation.NavigationIcon
import com.groupec.salesb.navigation.NavigationItem

@Composable
fun MyNavigationRail(navController: NavController, modifier: Modifier = Modifier) {

    // Observer la destination actuelle
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: NavigationItem.Home.route

   // var selectedItem by remember { mutableStateOf(NavigationItem.Home.route) }
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.SaveSale,
        NavigationItem.MySales,
        NavigationItem.Product
    )

    NavigationRail(
        containerColor = Silver,
        modifier = modifier,
    ) {
        items.forEach { item ->
            NavigationRailItem(
                icon = {
                    when (item.icon) {
                        is NavigationIcon.VectorIcon -> {
                            Icon(
                                imageVector = item.icon.imageVector,
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        is NavigationIcon.DrawableIcon -> {
                            Icon(
                                painter = painterResource(item.icon.drawableRes),
                                contentDescription = stringResource(id = item.title),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        null -> {
                            // No icon
                        }
                    }
                },
                label = { Text(
                    stringResource(id = item.title), fontSize = 16.sp,
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
    MyNavigationRail(rememberNavController())
}