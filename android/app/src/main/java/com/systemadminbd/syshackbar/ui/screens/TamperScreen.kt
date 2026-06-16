package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.ToolResult
import com.systemadminbd.syshackbar.data.WebToolsService
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

private data class HeaderRow(var key: String, var value: String)

/** Tamper Data: send a GET with custom headers and inspect the raw response headers. */
@Composable
fun TamperScreen(navController: NavController) {
    val scope = rememberCoroutineScope()
    var url by remember { mutableStateOf("") }
    val headers = remember { mutableStateListOf(HeaderRow("User-Agent", "Mozilla/5.0")) }
    var running by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<ToolResult?>(null) }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "Tamper Data",
            subtitle = "custom headers · no redirect",
            accent = AlertRed
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PromptLabel("target url", color = AlertRed)
                Spacer(Modifier.height(8.dp))
                TerminalField(value = url, onValueChange = { url = it }, placeholder = "http://example.com/", accent = AlertRed)

                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PromptLabel("request headers", color = AlertRed)
                    Spacer(Modifier.weight(1f))
                    Row(
                        Modifier
                            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                            .clickable { headers.add(HeaderRow("", "")) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, tint = AlertRed, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("ADD", color = AlertRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                headers.forEachIndexed { i, row ->
                    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.weight(0.42f)) {
                            TerminalField(value = row.key, onValueChange = { headers[i] = row.copy(key = it) }, placeholder = "Header", accent = AlertRed)
                        }
                        Spacer(Modifier.width(6.dp))
                        Box(Modifier.weight(0.5f)) {
                            TerminalField(value = row.value, onValueChange = { headers[i] = row.copy(value = it) }, placeholder = "Value", accent = AlertRed)
                        }
                        Spacer(Modifier.width(4.dp))
                        Box(
                            Modifier.size(30.dp).clickable { headers.removeAt(i) },
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.Close, contentDescription = null, tint = TextFaint, modifier = Modifier.size(15.dp)) }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(if (url.isBlank()) TerminalSurface else AlertRed, RoundedCornerShape(12.dp))
                        .border(1.dp, if (url.isBlank()) TerminalBorder else AlertRed, RoundedCornerShape(12.dp))
                        .clickable(enabled = url.isNotBlank() && !running) {
                            running = true
                            result = null
                            val map = headers.filter { it.key.isNotBlank() }.associate { it.key to it.value }
                            scope.launch {
                                result = WebToolsService.fetchHeaders(url, map)
                                running = false
                            }
                        }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (running) {
                        CircularProgressIndicator(Modifier.size(18.dp), color = com.systemadminbd.syshackbar.ui.theme.TerminalBg, strokeWidth = 2.dp)
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Send, contentDescription = null, tint = if (url.isBlank()) TextFaint else com.systemadminbd.syshackbar.ui.theme.TerminalBg, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("SEND REQUEST", color = if (url.isBlank()) TextFaint else com.systemadminbd.syshackbar.ui.theme.TerminalBg, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                result?.let { r ->
                    PromptLabel("response", color = AlertRed)
                    Spacer(Modifier.height(8.dp))
                    when (r) {
                        is ToolResult.Lines -> OutputBox(output = r.items.joinToString("\n"), isError = false, accent = MatrixGreen)
                        is ToolResult.Text -> OutputBox(output = r.body, isError = false, accent = MatrixGreen)
                        is ToolResult.Error -> OutputBox(output = "✗ ${r.message}", isError = true, accent = AlertRed)
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}
