package com.systemadminbd.syshackbar.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Lightweight app settings: tracks first-run disclaimer acceptance. */
class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = app.getSharedPreferences("syshackbar_settings", Application.MODE_PRIVATE)

    private val _disclaimerAccepted = MutableStateFlow(prefs.getBoolean(KEY_DISCLAIMER, false))
    val disclaimerAccepted: StateFlow<Boolean> = _disclaimerAccepted.asStateFlow()

    fun acceptDisclaimer() {
        prefs.edit().putBoolean(KEY_DISCLAIMER, true).apply()
        _disclaimerAccepted.value = true
    }

    companion object {
        private const val KEY_DISCLAIMER = "disclaimer_accepted"
        const val APP_VERSION = "1.0.0"
    }
}
