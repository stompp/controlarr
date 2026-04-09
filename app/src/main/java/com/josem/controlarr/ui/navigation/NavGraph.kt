package com.josem.controlarr.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.josem.controlarr.ui.screens.AddEditServerScreen
import com.josem.controlarr.ui.screens.HomeScreen
import com.josem.controlarr.ui.screens.HostServicesScreen
import com.josem.controlarr.ui.screens.HostsScreen
import com.josem.controlarr.ui.screens.WebViewScreen
import com.josem.controlarr.viewmodel.ServerViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: ServerViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onAddServer = { navController.navigate(Screen.AddServer.createRoute()) },
                onEditServer = { id -> navController.navigate(Screen.EditServer.createRoute(id)) },
                onOpenServer = { id -> navController.navigate(Screen.WebView.createRoute(id)) },
                onManageHosts = { navController.navigate(Screen.Hosts.route) },
                onOpenHostServices = { hostId ->
                    navController.navigate(Screen.HostServices.createRoute(hostId))
                }
            )
        }

        composable(Screen.Hosts.route) {
            HostsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.HostServices.route,
            arguments = listOf(navArgument("hostId") { type = NavType.IntType })
        ) { backStackEntry ->
            val hostId = backStackEntry.arguments?.getInt("hostId") ?: return@composable
            HostServicesScreen(
                viewModel = viewModel,
                hostId = hostId,
                onNavigateBack = { navController.popBackStack() },
                onAddServer = { hId ->
                    navController.navigate(Screen.AddServer.createRoute(hId))
                },
                onEditServer = { id -> navController.navigate(Screen.EditServer.createRoute(id)) },
                onOpenServer = { id -> navController.navigate(Screen.WebView.createRoute(id)) }
            )
        }

        composable(
            route = Screen.AddServer.route,
            arguments = listOf(
                navArgument("hostId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val hostId = backStackEntry.arguments?.getInt("hostId")?.takeIf { it > 0 }
            AddEditServerScreen(
                viewModel = viewModel,
                serverId = null,
                preselectedHostId = hostId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditServer.route,
            arguments = listOf(navArgument("serverId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getInt("serverId") ?: return@composable
            AddEditServerScreen(
                viewModel = viewModel,
                serverId = serverId,
                preselectedHostId = null,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WebView.route,
            arguments = listOf(navArgument("serverId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serverId = backStackEntry.arguments?.getInt("serverId") ?: return@composable
            WebViewScreen(
                viewModel = viewModel,
                serverId = serverId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
