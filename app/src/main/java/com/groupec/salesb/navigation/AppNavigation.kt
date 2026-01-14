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
    SaveSale,
    MySales,
    Product,
    ProductDetail,
    Category,
    Rayon,
    SaleChart,
    ForgotPassword,
    Outputs,
    OutputDetail,
    Account,
    AccountDetail,
    TermsAndConditions

}
sealed class NavigationItem(val route: String,val title: Int = 0, val icon: NavigationIcon ? = null) {
    data object Loading : NavigationItem(Screen.Loading.name)
    data object Configuration : NavigationItem(Screen.Configuration.name)
    data object Login : NavigationItem(Screen.Login.name)
    data object ChangePassword : NavigationItem(Screen.ChangePassword.name)
    data object Home : NavigationItem(Screen.Home.name, R.string.menu_home, NavigationIcon.VectorIcon(AppIcons.Home))
    data object SaveSale : NavigationItem(Screen.SaveSale.name, R.string.menu_save_sale, NavigationIcon.VectorIcon(AppIcons.SaveSale))
    data object MySales : NavigationItem(Screen.MySales.name, R.string.menu_my_sales, NavigationIcon.VectorIcon(AppIcons.MySales))
    data object Product : NavigationItem(Screen.Product.name, R.string.menu_product, NavigationIcon.DrawableIcon(AppIcons.Product))
    data object Outputs : NavigationItem(Screen.Outputs.name, R.string.menu_outputs, NavigationIcon.VectorIcon(AppIcons.Output))
    data object ProductDetail : NavigationItem(Screen.ProductDetail.name)
    data object OutputDetail : NavigationItem(Screen.OutputDetail.name)
    data object Category : NavigationItem(Screen.Category.name)
    data object Rayon : NavigationItem(Screen.Rayon.name, R.string.menu_rayon)
    data object SaleChart : NavigationItem(Screen.SaleChart.name)
    data object ForgotPassword : NavigationItem(Screen.ForgotPassword.name)
    data object Account : NavigationItem(Screen.Account.name)
    data object AccountDetail : NavigationItem(Screen.AccountDetail.name)
    data object TermsAndConditions : NavigationItem(Screen.TermsAndConditions.name)
}

sealed class NavigationIcon {
    data class VectorIcon(val imageVector: ImageVector) : NavigationIcon()
    data class DrawableIcon(@DrawableRes val drawableRes: Int) : NavigationIcon()
}
