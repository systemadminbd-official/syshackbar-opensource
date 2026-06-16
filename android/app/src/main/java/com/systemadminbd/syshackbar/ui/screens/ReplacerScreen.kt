package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.ConvertFormat
import com.systemadminbd.syshackbar.data.Converters
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.WarnAmber
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint

/**
 * Replacer tool: type a string and instantly see it converted into every
 * supported format side-by-side (String → Hex/URL/Base64/Binary/…).
 */
@Composable
fun ReplacerScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var selector by remember { mutableStateOf("") }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "Replacer",
            subtitle = "string → all formats",
            accent = WarnAmber
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PromptLabel("input string", color = WarnAmber)
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = "type text to convert…",
                    accent = WarnAmber,
                    minLines = 2
                )
                Spacer(Modifier.height(18.dp))
                PromptLabel("conversions", color = WarnAmber)
                Spacer(Modifier.height(8.dp))
                if (input.isEmpty()) {
                    Text(
                        "// every format updates live as you type",
                        color = TextFaint,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    ConvertFormat.entries.forEach { f ->
                        val out = Converters.encode(input, f)
                        ResultLine(f.label, out)
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(18.dp))
                PromptLabel("char-by-char replace", color = WarnAmber)
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = selector,
                    onValueChange = { selector = it },
                    placeholder = "e.g. ' < > to URL-encode each char",
                    accent = WarnAmber
                )
                Spacer(Modifier.height(8.dp))
                if (selector.isNotEmpty()) {
                    val urlEach = selector.map { Converters.encode(it.toString(), ConvertFormat.URL) }.joinToString("")
                    val hexEach = selector.map { "0x" + Converters.encode(it.toString(), ConvertFormat.HEX) }.joinToString(",")
                    ResultLine("URL each", urlEach)
                    Spacer(Modifier.height(10.dp))
                    ResultLine("Hex each", hexEach)
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun ResultLine(label: String, value: String) {
    OutputBox(output = if (value.isEmpty()) "" else "[$label]\n$value", isError = false, accent = WarnAmber)
}
