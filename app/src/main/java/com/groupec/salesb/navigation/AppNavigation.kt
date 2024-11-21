package com.groupec.salesb.navigation

enum class Screen {
    Loading,
    Configuration,
    Login,
    ChangePassword,
    Home
}
sealed class NavigationItem(val route: String) {
    data object Loading : NavigationItem(Screen.Loading.name)
    data object Configuration : NavigationItem(Screen.Configuration.name)
    data object Login : NavigationItem(Screen.Login.name)
    data object ChangePassword : NavigationItem(Screen.ChangePassword.name)
    data object Home : NavigationItem(Screen.Home.name)
}