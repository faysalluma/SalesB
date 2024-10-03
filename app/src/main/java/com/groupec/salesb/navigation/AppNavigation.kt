package com.groupec.salesb.navigation

enum class Screen {
    Loading,
    Configuration,
    Login,
    ChangePassword,
    Home
}
sealed class NavigationItem(val route: String) {
    object Loading : NavigationItem(Screen.Loading.name)
    object Configuration : NavigationItem(Screen.Configuration.name)
    object Login : NavigationItem(Screen.Login.name)
    object ChangePassword : NavigationItem(Screen.ChangePassword.name)
    object Home : NavigationItem(Screen.Home.name)
}