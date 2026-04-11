package com.josem.controlarr

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.josem.controlarr.ui.navigation.NavGraph
import com.josem.controlarr.ui.screens.AppLockScreen
import com.josem.controlarr.ui.theme.ControlarrTheme
import com.josem.controlarr.viewmodel.ServerViewModel

class MainActivity : FragmentActivity() {

    private var isUnlocked by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as ControlarrApp
        val db = app.database
        val encryptedPrefs = app.encryptedPrefs

        setContent {
            ControlarrTheme {
                if (encryptedPrefs.appLockEnabled && !isUnlocked) {
                    AppLockScreen(
                        encryptedPrefs = encryptedPrefs,
                        activity = this@MainActivity,
                        onUnlocked = { isUnlocked = true }
                    )
                } else {
                    val viewModel: ServerViewModel = viewModel(
                        factory = ServerViewModel.factory(
                            db.serverDao(),
                            db.hostDao(),
                            db.tokenDao(),
                            encryptedPrefs
                        )
                    )
                    val navController = rememberNavController()
                    NavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
