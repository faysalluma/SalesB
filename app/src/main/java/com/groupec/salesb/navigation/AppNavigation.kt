package com.groupec.salesb.navigation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.groupec.salesb.R
import com.groupec.salesb.core.designsystem.icon.AppIcons

enum class Screen {
    Loading,
    Configuration,
    Login,
    ChangePassword,
    Home,
    Sale,
    Product,
    ProductDetail

}
sealed class NavigationItem(val route: String,val title: Int = 0, val icon: NavigationIcon ? = null) {
    data object Loading : NavigationItem(Screen.Loading.name)
    data object Configuration : NavigationItem(Screen.Configuration.name)
    data object Login : NavigationItem(Screen.Login.name)
    data object ChangePassword : NavigationItem(Screen.ChangePassword.name)
    data object Home : NavigationItem(Screen.Home.name, R.string.menu_home, NavigationIcon.VectorIcon(AppIcons.Home))
    data object Sale : NavigationItem(Screen.Sale.name, R.string.menu_sale, NavigationIcon.DrawableIcon(AppIcons.Sale))
    data object Product : NavigationItem(Screen.Product.name, R.string.menu_product, NavigationIcon.DrawableIcon(AppIcons.Product))
    data object ProductDetail : NavigationItem(Screen.ProductDetail.name)
}

sealed class NavigationIcon {
    data class VectorIcon(val imageVector: ImageVector) : NavigationIcon()
    data class DrawableIcon(@DrawableRes val drawableRes: Int) : NavigationIcon()
}