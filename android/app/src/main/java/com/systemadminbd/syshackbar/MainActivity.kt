package com.systemadminbd.syshackbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.systemadminbd.syshackbar.data.BrandGuard
import com.systemadminbd.syshackbar.data.SettingsViewModel
import com.systemadminbd.syshackbar.ui.navigation.AppNavigation
import com.systemadminbd.syshackbar.ui.screens.DisclaimerScreen
import com.systemadminbd.syshackbar.ui.screens.SplashScreen
import com.systemadminbd.syshackbar.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // SystemAdminBD branding integrity guard — refuses to run if tampered.
        BrandGuard.verify(applicationContext)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                RootGate()
            }
        }
    }
}

/** Splash → first-run disclaimer → main app. */
@Composable
private fun RootGate() {
    val settings: SettingsViewModel = viewModel()
    val accepted by settings.disclaimerAccepted.collectAsState()
    var splashDone by remember { mutableStateOf(false) }

    when {
        !splashDone -> SplashScreen(onDone = { splashDone = true })
        !accepted -> DisclaimerScreen(onAccept = { settings.acceptDisclaimer() })
        else -> AppNavigation()
    }
}
