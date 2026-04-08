package com.josem.controlarr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.josem.controlarr.ui.navigation.NavGraph
import com.josem.controlarr.ui.theme.ControlarrTheme
import com.josem.controlarr.viewmodel.ServerViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dao = (application as ControlarrApp).database.serverDao()
        setContent {
            ControlarrTheme {
                val viewModel: ServerViewModel = viewModel(factory = ServerViewModel.factory(dao))
                val navController = rememberNavController()
                NavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}
