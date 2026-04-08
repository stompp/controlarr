package com.josem.controlarr.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object AddServer : Screen("add_server")
    data object EditServer : Screen("edit_server/{serverId}") {
        fun createRoute(serverId: Int) = "edit_server/$serverId"
    }
    data object WebView : Screen("webview/{serverId}") {
        fun createRoute(serverId: Int) = "webview/$serverId"
    }
}
