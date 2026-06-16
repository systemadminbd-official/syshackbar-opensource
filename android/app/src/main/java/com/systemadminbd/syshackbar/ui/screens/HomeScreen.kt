package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.FindReplace
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.PayloadCategory
import com.systemadminbd.syshackbar.data.PayloadGroup
import com.systemadminbd.syshackbar.data.PayloadLibrary
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
import com.systemadminbd.syshackbar.ui.theme.WarnAmber

private data class Tool(
    val route: String,
    val name: String,
    val desc: String,
    val icon: ImageVector,
    val accent: Color
)

@Composable
fun HomeScreen(navController: NavController) {
    val tools = listOf(
        Tool("encoder", "Encoder / Decoder", "Base64 · URL · Hex · Binary", Icons.Filled.Code, CyberCyan),
        Tool("replacer", "Replacer", "String → all formats at once", Icons.Filled.FindReplace, WarnAmber),
        Tool("hash", "Hash Generator", "MD5 · SHA-1 · SHA-256 · 512", Icons.Filled.Tag, Violet),
        Tool("strtools", "String Lab", "Slashes · case · buffer · int/hex", Icons.Filled.TextFields, MatrixGreen),
    )

    HackerBackground {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 64.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Header() }
            item { Spacer(Modifier.height(4.dp)) }
            item { PromptLabel("tools", color = CyberCyan) }
            items(tools) { tool ->
                ToolCard(tool) { navController.navigate(tool.route) }
            }
            PayloadGroup.entries.forEach { group ->
                val cats = PayloadLibrary.byGroup(group)
                if (cats.isNotEmpty()) {
                    item { Spacer(Modifier.height(8.dp)) }
                    item { PromptLabel(group.label, color = MatrixGreen) }
                    items(cats) { cat ->
                        CategoryCard(cat) { navController.navigate("category/${cat.id}") }
                    }
                }
            }
            item { Footer() }
        }
    }
}

@Composable
private fun Header() {
    val transition = rememberInfiniteTransition(label = "cursor")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "blink"
    )
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Sys",
                color = MatrixGreen,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Text(
                "HackBar",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp
            )
            Box(
                Modifier
                    .padding(start = 4.dp, top = 6.dp)
                    .size(width = 12.dp, height = 24.dp)
                    .background(MatrixGreen.copy(alpha = alpha))
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "// android pentest toolkit",
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun ToolCard(tool: Tool, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(14.dp))
            .background(TerminalSurface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .background(tool.accent.copy(alpha = 0.12f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(tool.icon, contentDescription = null, tint = tool.accent, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                tool.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(tool.desc, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextFaint
        )
    }
}

@Composable
private fun CategoryCard(cat: PayloadCategory, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(14.dp))
            .background(TerminalSurface, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(4.dp)
                .height(38.dp)
                .background(cat.accent, RoundedCornerShape(2.dp))
        )
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                cat.name,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(cat.tagline, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Text(
            "${cat.payloads.size}",
            color = cat.accent,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
        Spacer(Modifier.width(4.dp))
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = TextFaint
        )
    }
}

@Composable
private fun Footer() {
    Column(Modifier.padding(top = 20.dp)) {
        Text(
            "// for authorized testing & education only",
            color = TextFaint,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
        Text(
            "// SysHackBar — inspired build",
            color = TextFaint,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp
        )
    }
}
