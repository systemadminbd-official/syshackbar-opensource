package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.ConvertFormat
import com.systemadminbd.syshackbar.data.Converters
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.CyberCyan
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary

@Composable
fun EncoderScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var format by remember { mutableStateOf(ConvertFormat.BASE64) }
    var decodeMode by remember { mutableStateOf(false) }

    val output: String
    val isError: Boolean
    if (input.isEmpty()) {
        output = ""
        isError = false
    } else if (decodeMode) {
        val decoded = Converters.decode(input, format)
        output = decoded ?: "✗ invalid ${format.label} input"
        isError = decoded == null
    } else {
        output = Converters.encode(input, format)
        isError = false
    }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "Encoder / Decoder",
            subtitle = if (decodeMode) "decode mode" else "encode mode",
            accent = CyberCyan
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PromptLabel("format", color = CyberCyan)
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ConvertFormat.entries.forEach { f ->
                        FormatChip(f.label, selected = f == format, accent = CyberCyan) { format = f }
                    }
                }

                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PromptLabel(if (decodeMode) "encoded input" else "plain input", color = CyberCyan)
                    Spacer(Modifier.weight(1f))
                    Row(
                        modifier = Modifier
                            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                            .clickable { decodeMode = !decodeMode }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.SwapVert, contentDescription = null, tint = CyberCyan, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            if (decodeMode) "DECODE" else "ENCODE",
                            color = CyberCyan,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = "type or paste text…",
                    accent = CyberCyan,
                    minLines = 3
                )

                Spacer(Modifier.height(18.dp))
                PromptLabel("output", color = CyberCyan)
                Spacer(Modifier.height(8.dp))
                OutputBox(output = output, isError = isError, accent = CyberCyan)
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FormatChip(label: String, selected: Boolean, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .border(1.dp, if (selected) accent else TerminalBorder, RoundedCornerShape(9.dp))
            .background(if (selected) accent.copy(alpha = 0.14f) else TerminalSurface, RoundedCornerShape(9.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            label,
            color = if (selected) accent else TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 12.sp
        )
    }
}

@Composable
fun OutputBox(output: String, isError: Boolean, accent: Color) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1100)
            copied = false
        }
    }
    val color = if (isError) AlertRed else accent
    Box(
        Modifier
            .fillMaxWidth()
            .border(1.dp, if (output.isEmpty()) TerminalBorder else color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .background(TerminalSurface, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        if (output.isEmpty()) {
            Text("// result appears here", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
        } else {
            Column {
                Text(
                    output,
                    color = color,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
                if (!isError) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .border(1.dp, accent.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .clickable {
                                clipboard.setText(AnnotatedString(output))
                                copied = true
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (copied) "COPIED" else "COPY",
                            color = accent,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
