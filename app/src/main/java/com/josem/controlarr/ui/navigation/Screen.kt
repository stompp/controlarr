package com.josem.controlarr.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Hosts : Screen("hosts")
    data object HostServices : Screen("host_services/{hostId}") {
        fun createRoute(hostId: Int) = "host_services/$hostId"
    }
    data object AddServer : Screen("add_server?hostId={hostId}") {
        fun createRoute(hostId: Int? = null) =
            if (hostId != null) "add_server?hostId=$hostId" else "add_server"
    }
    data object EditServer : Screen("edit_server/{serverId}") {
        fun createRoute(serverId: Int) = "edit_server/$serverId"
    }
    data object WebView : Screen("webview/{serverId}") {
        fun createRoute(serverId: Int) = "webview/$serverId"
    }
    data object TokenManager : Screen("token_manager")
    data object SecuritySettings : Screen("security_settings")
}
