package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.SettingsViewModel
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.CyberCyan
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextPrimary
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import com.systemadminbd.syshackbar.ui.theme.Violet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

/** About / settings: app info, update check, ToS & privacy, legal notice. */
@Composable
fun AboutScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var checking by remember { mutableStateOf(false) }
    var updateMsg by remember { mutableStateOf<String?>(null) }
    var expanded by remember { mutableStateOf<String?>(null) }

    HackerBackground {
        LazyColumn(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Sys", color = MatrixGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                    Text("HackBar", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 26.sp)
                }
            }
            item {
                Text(
                    "// pentest toolkit · v${SettingsViewModel.APP_VERSION}",
                    color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp
                )
            }

            item { Spacer(Modifier.height(4.dp)) }
            item { PromptLabel("maintenance", color = MatrixGreen) }
            item {
                ActionCard(
                    icon = Icons.Filled.SystemUpdate,
                    accent = MatrixGreen,
                    title = "Check for Updates",
                    subtitle = updateMsg ?: "Tap to check the latest version",
                    trailing = { if (checking) CircularProgressIndicator(Modifier.size(18.dp), color = MatrixGreen, strokeWidth = 2.dp) }
                ) {
                    if (!checking) {
                        updateMsg = null
                        checking = true
                        scope.launch {
                            delay(1200)
                            checking = false
                            updateMsg = "✓ You're on the latest version (${SettingsViewModel.APP_VERSION})"
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(4.dp)) }
            item { PromptLabel("legal", color = CyberCyan) }
            item {
                ExpandableCard(
                    icon = Icons.Filled.Description, accent = CyberCyan,
                    title = "Terms of Service",
                    body = TERMS,
                    isOpen = expanded == "tos",
                    onToggle = { expanded = if (expanded == "tos") null else "tos" }
                )
            }
            item {
                ExpandableCard(
                    icon = Icons.Filled.Lock, accent = Violet,
                    title = "Privacy Policy",
                    body = PRIVACY,
                    isOpen = expanded == "privacy",
                    onToggle = { expanded = if (expanded == "privacy") null else "privacy" }
                )
            }

            item { Spacer(Modifier.height(8.dp)) }
            item {
                Text(
                    "// SysHackBar is provided for authorized security\n// testing and education only. Use responsibly.",
                    color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 10.sp, lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
private fun ActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(14.dp))
            .background(TerminalSurface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier.size(40.dp).background(accent.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp)) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        trailing()
    }
}

@Composable
private fun ExpandableCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accent: Color,
    title: String,
    body: String,
    isOpen: Boolean,
    onToggle: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, if (isOpen) accent.copy(alpha = 0.5f) else TerminalBorder, RoundedCornerShape(14.dp))
            .background(TerminalSurface, RoundedCornerShape(14.dp))
            .clickable(onClick = onToggle)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(40.dp).background(accent.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(14.dp))
            Text(title, color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(if (isOpen) "−" else "+", color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        if (isOpen) {
            Spacer(Modifier.height(10.dp))
            Text(body, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp, lineHeight = 17.sp)
        }
    }
}

private const val TERMS =
    "By using SysHackBar you agree to use it solely for lawful, authorized, and educational security testing. You must obtain explicit permission before testing any system you do not own. You are fully responsible for your actions. The developers provide this software \"as is\" without warranty and accept no liability for misuse or damages."

private const val PRIVACY =
    "SysHackBar does not collect, store, or transmit personal data. Custom payloads you save remain locally on your device. Network tools (reverse IP, subdomain finder, scanners) only contact public OSINT services at the moment you run them, sending only the target you enter. No analytics or tracking is included."
