package com.groupec.salesb.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
    CategoryDetail,
    Client,
    ClientDetail,
    Rayon,
    RayonDetail,
    SaleChart,
    ForgotPassword,
    Outputs,
    OutputDetail,
    Account,
    AccountDetail,
    HandleService,
    UpdateBusinessInfo,
    TermsAndConditions,
    Signup,
    Subscription,
    PrintReceiptGuide,
    Faq
}
sealed class NavigationItem(
    val route: String,
    @StringRes val title: Int,
    val icon: NavigationIcon? = null,
) {
    data object Loading : NavigationItem(Screen.Loading.name, R.string.screen_title_loading)
    data object Configuration : NavigationItem(Screen.Configuration.name, R.string.screen_title_configuration)
    data object Login : NavigationItem(Screen.Login.name, R.string.screen_title_login)
    data object ChangePassword : NavigationItem(Screen.ChangePassword.name, R.string.screen_title_change_password)
    data object Home : NavigationItem(Screen.Home.name, R.string.menu_home, NavigationIcon.VectorIcon(AppIcons.Home))
    data object SaveSale : NavigationItem(Screen.SaveSale.name, R.string.menu_save_sale, NavigationIcon.VectorIcon(AppIcons.SaveSale))
    data object MySales : NavigationItem(Screen.MySales.name, R.string.screen_title_sales, NavigationIcon.VectorIcon(AppIcons.MySales))
    data object Product : NavigationItem(Screen.Product.name, R.string.screen_title_products, NavigationIcon.DrawableIcon(AppIcons.Product))
    data object Outputs : NavigationItem(Screen.Outputs.name, R.string.screen_title_outputs, NavigationIcon.VectorIcon(AppIcons.Output))
    data object ProductDetail : NavigationItem(Screen.ProductDetail.name, R.string.screen_title_product_detail)
    data object OutputDetail : NavigationItem(Screen.OutputDetail.name, R.string.screen_title_output_detail)
    data object Category : NavigationItem(Screen.Category.name, R.string.screen_title_categories)
    data object CategoryDetail : NavigationItem(Screen.CategoryDetail.name, R.string.screen_title_category_detail)
    data object Client : NavigationItem(Screen.Client.name, R.string.screen_title_clients)
    data object ClientDetail : NavigationItem(Screen.ClientDetail.name, R.string.screen_title_client_detail)
    data object Rayon : NavigationItem(Screen.Rayon.name, R.string.screen_title_rayons)
    data object RayonDetail : NavigationItem(Screen.RayonDetail.name, R.string.screen_title_rayon_detail)
    data object SaleChart : NavigationItem(Screen.SaleChart.name, R.string.screen_title_sale_chart)
    data object ForgotPassword : NavigationItem(Screen.ForgotPassword.name, R.string.screen_title_forgot_password)
    data object Account : NavigationItem(Screen.Account.name, R.string.screen_title_accounts)
    data object AccountDetail : NavigationItem(Screen.AccountDetail.name, R.string.screen_title_account_detail)
    data object HandleService : NavigationItem(Screen.HandleService.name, R.string.screen_title_options)
    data object UpdateBusinessInfo : NavigationItem(Screen.UpdateBusinessInfo.name, R.string.screen_title_business_info)
    data object TermsAndConditions : NavigationItem(Screen.TermsAndConditions.name, R.string.screen_title_terms)
    data object Signup : NavigationItem(Screen.Signup.name, R.string.screen_title_signup)
    data object Subscription : NavigationItem(Screen.Subscription.name, R.string.screen_title_subscription)
    data object PrintReceiptGuide : NavigationItem(Screen.PrintReceiptGuide.name, R.string.screen_title_print_guide)
    data object Faq : NavigationItem(Screen.Faq.name, R.string.screen_title_faq)

    companion object {
        fun fromRoute(route: String): NavigationItem = when (route) {
            Loading.route -> Loading
            Configuration.route -> Configuration
            Login.route -> Login
            ChangePassword.route -> ChangePassword
            Home.route -> Home
            SaveSale.route -> SaveSale
            MySales.route -> MySales
            Product.route -> Product
            Outputs.route -> Outputs
            ProductDetail.route -> ProductDetail
            OutputDetail.route -> OutputDetail
            Category.route -> Category
            CategoryDetail.route -> CategoryDetail
            Client.route -> Client
            ClientDetail.route -> ClientDetail
            Rayon.route -> Rayon
            RayonDetail.route -> RayonDetail
            SaleChart.route -> SaleChart
            ForgotPassword.route -> ForgotPassword
            Account.route -> Account
            AccountDetail.route -> AccountDetail
            HandleService.route -> HandleService
            UpdateBusinessInfo.route -> UpdateBusinessInfo
            TermsAndConditions.route -> TermsAndConditions
            Signup.route -> Signup
            Subscription.route -> Subscription
            PrintReceiptGuide.route -> PrintReceiptGuide
            Faq.route -> Faq
            else -> error("No navigation item configured for route: $route")
        }
    }
}

sealed class NavigationIcon {
    data class VectorIcon(val imageVector: ImageVector) : NavigationIcon()
    data class DrawableIcon(@DrawableRes val drawableRes: Int) : NavigationIcon()
}
