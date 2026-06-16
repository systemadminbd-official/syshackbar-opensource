package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.HashAlgo
import com.systemadminbd.syshackbar.data.Hashing
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import com.systemadminbd.syshackbar.ui.theme.Violet

@Composable
fun HashScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    val results = remember(input) {
        if (input.isEmpty()) emptyList() else Hashing.all(input)
    }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "Hash Generator",
            subtitle = "one-way digests",
            accent = Violet
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PromptLabel("input", color = Violet)
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = "text to hash…",
                    accent = Violet,
                    minLines = 2
                )
                Spacer(Modifier.height(18.dp))
                PromptLabel("digests", color = Violet)
                Spacer(Modifier.height(8.dp))
                if (results.isEmpty()) {
                    Text(
                        "// enter text above",
                        color = TextFaint,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    results.forEach { (algo, digest) ->
                        HashRow(algo, digest)
                        Spacer(Modifier.height(10.dp))
                    }
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun HashRow(algo: HashAlgo, digest: String) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val border by animateColorAsState(if (copied) Violet else TerminalBorder, label = "b")
    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1100)
            copied = false
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .background(TerminalSurface, RoundedCornerShape(12.dp))
            .clickable {
                clipboard.setText(AnnotatedString(digest))
                copied = true
            }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text(
                algo.label,
                color = Violet,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                contentDescription = "Copy",
                tint = if (copied) Violet else TextFaint,
                modifier = Modifier.size(15.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Text(
            digest,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}
