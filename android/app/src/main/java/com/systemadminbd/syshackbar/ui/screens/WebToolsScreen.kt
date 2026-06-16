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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.CyberCyan
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextPrimary
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import com.systemadminbd.syshackbar.ui.theme.Violet
import com.systemadminbd.syshackbar.ui.theme.WarnAmber

data class WebTool(
    val id: String,
    val name: String,
    val desc: String,
    val icon: ImageVector,
    val accent: Color,
    val route: String
)

val webTools: List<WebTool> = listOf(
    WebTool("hash_crack", "Hash Decrypter", "Identify & crack MD5/SHA hashes", Icons.Filled.Lock, Violet, "webtool/hash_crack"),
    WebTool("sqli_scan", "SQLi Scanner", "Probe a URL param for SQL errors", Icons.Filled.Radar, AlertRed, "webtool/sqli_scan"),
    WebTool("reverse_ip", "Reverse IP Lookup", "Domains hosted on the same server", Icons.Filled.Public, CyberCyan, "webtool/reverse_ip"),
    WebTool("subdomains", "Subdomain Finder", "Enumerate subdomains of a domain", Icons.Filled.Hub, MatrixGreen, "webtool/subdomains"),
    WebTool("dns", "DNS Lookup", "A / MX / NS / TXT records", Icons.Filled.Dns, CyberCyan, "webtool/dns"),
    WebTool("whois", "Whois Lookup", "Registrar, owner & registration data", Icons.Filled.Badge, MatrixGreen, "webtool/whois"),
    WebTool("headers", "HTTP Headers Inspector", "Inspect full response headers", Icons.Filled.Http, Violet, "webtool/headers"),
    WebTool("port_scan", "Port Scanner", "Scan common TCP ports for open services", Icons.Filled.Lan, AlertRed, "webtool/port_scan"),
    WebTool("admin_finder", "Admin Panel Finder", "Scan common admin paths", Icons.Filled.AdminPanelSettings, WarnAmber, "webtool/admin_finder"),
    WebTool("tamper", "Tamper Data", "Custom headers · inspect response", Icons.Filled.Tune, AlertRed, "tamper"),
)

@Composable
fun WebToolsScreen(navController: NavController) {
    HackerBackground {
        LazyColumn(
            Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Build, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Web Tools", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
            item {
                Text(
                    "// live recon · runs against the target you enter",
                    color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp
                )
            }
            item { Spacer(Modifier.height(4.dp)) }
            item { PromptLabel("recon & scanning", color = CyberCyan) }
            items(webTools) { tool ->
                ToolRow(tool) { navController.navigate(tool.route) }
            }
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "// only test systems you are authorized to assess",
                    color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ToolRow(tool: WebTool, onClick: () -> Unit) {
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
            Modifier.size(44.dp).background(tool.accent.copy(alpha = 0.12f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) { Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(22.dp)) }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(tool.name, color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(Modifier.height(2.dp))
            Text(tool.desc, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = TextFaint)
    }
}
