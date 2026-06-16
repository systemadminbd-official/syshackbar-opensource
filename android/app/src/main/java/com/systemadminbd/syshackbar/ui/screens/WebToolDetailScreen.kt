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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.ToolResult
import com.systemadminbd.syshackbar.data.WebToolsViewModel
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBg
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextPrimary
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import androidx.compose.runtime.collectAsState

private data class ToolSpec(val title: String, val sub: String, val placeholder: String, val accent: Color, val cta: String)

private fun specFor(id: String): ToolSpec = when (id) {
    "hash_crack" -> ToolSpec("Hash Decrypter", "identify & crack", "paste a hash (md5 / sha1 / sha256 / sha512)", com.systemadminbd.syshackbar.ui.theme.Violet, "CRACK")
    "sqli_scan" -> ToolSpec("SQLi Scanner", "error-based probe", "http://site.com/page.php?id=1", AlertRed, "SCAN")
    "reverse_ip" -> ToolSpec("Reverse IP Lookup", "shared hosting recon", "example.com or 93.184.216.34", com.systemadminbd.syshackbar.ui.theme.CyberCyan, "LOOKUP")
    "subdomains" -> ToolSpec("Subdomain Finder", "host enumeration", "example.com", MatrixGreen, "FIND")
    "dns" -> ToolSpec("DNS Lookup", "dns records", "example.com", com.systemadminbd.syshackbar.ui.theme.CyberCyan, "RESOLVE")
    "whois" -> ToolSpec("Whois Lookup", "registration data", "example.com or 93.184.216.34", MatrixGreen, "LOOKUP")
    "headers" -> ToolSpec("HTTP Headers Inspector", "response headers", "https://example.com", com.systemadminbd.syshackbar.ui.theme.Violet, "INSPECT")
    "port_scan" -> ToolSpec("Port Scanner", "common tcp ports", "example.com or 93.184.216.34", AlertRed, "SCAN")
    "admin_finder" -> ToolSpec("Admin Panel Finder", "path scanner", "example.com", com.systemadminbd.syshackbar.ui.theme.WarnAmber, "SCAN")
    else -> ToolSpec("Web Tool", "", "enter target", MatrixGreen, "RUN")
}

@Composable
fun WebToolDetailScreen(navController: NavController, toolId: String) {
    val vm: WebToolsViewModel = viewModel()
    val state by vm.state.collectAsState()
    val spec = remember(toolId) { specFor(toolId) }
    var input by remember { mutableStateOf("") }

    DisposableEffect(toolId) { onDispose { vm.reset() } }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = spec.title,
            subtitle = spec.sub,
            accent = spec.accent
        ) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    PromptLabel("target", color = spec.accent)
                    Spacer(Modifier.height(8.dp))
                    TerminalField(value = input, onValueChange = { input = it }, placeholder = spec.placeholder, accent = spec.accent)
                    Spacer(Modifier.height(12.dp))
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .background(if (input.isBlank()) TerminalSurface else spec.accent, RoundedCornerShape(12.dp))
                            .border(1.dp, if (input.isBlank()) TerminalBorder else spec.accent, RoundedCornerShape(12.dp))
                            .clickable(enabled = input.isNotBlank() && !state.running) { vm.run(toolId, input) }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.running) {
                            CircularProgressIndicator(Modifier.size(18.dp), color = TerminalBg, strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Bolt, contentDescription = null, tint = if (input.isBlank()) TextFaint else TerminalBg, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(spec.cta, color = if (input.isBlank()) TextFaint else TerminalBg, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                val result = state.result
                if (state.running) {
                    item {
                        Text("// running… contacting target", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                    }
                } else if (result != null) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PromptLabel("result", color = spec.accent)
                            if (result is ToolResult.Lines) {
                                Spacer(Modifier.width(8.dp))
                                Text("${result.items.size}", color = spec.accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                    when (result) {
                        is ToolResult.Error -> item {
                            Box(
                                Modifier.fillMaxWidth()
                                    .border(1.dp, AlertRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .background(TerminalSurface, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text("✗ ${result.message}", color = AlertRed, fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp)
                            }
                        }
                        is ToolResult.Text -> item {
                            Box(
                                Modifier.fillMaxWidth()
                                    .border(1.dp, spec.accent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                    .background(TerminalSurface, RoundedCornerShape(12.dp))
                                    .padding(14.dp)
                            ) {
                                Text(result.body, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp)
                            }
                        }
                        is ToolResult.Lines -> items(result.items) { line ->
                            ResultRow(line, spec.accent)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                } else {
                    item {
                        Text(
                            "// enter a target and tap ${spec.cta.lowercase()}",
                            color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultRow(line: String, accent: Color) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    Row(
        Modifier.fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
            .background(TerminalSurface, RoundedCornerShape(10.dp))
            .clickable { clipboard.setText(AnnotatedString(line)); copied = true }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(6.dp).background(accent, RoundedCornerShape(2.dp)))
        Spacer(Modifier.width(10.dp))
        Text(line, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f))
        Icon(Icons.Filled.ContentCopy, contentDescription = "Copy", tint = if (copied) accent else TextFaint, modifier = Modifier.size(14.dp))
    }
}
