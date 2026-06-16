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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.StringOps
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PromptLabel
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TextFaint

/** A single string-manipulation operation. */
private data class StrOp(val label: String, val transform: (String) -> String)

/**
 * String Lab — classic HackBar "OTHER" utilities: slashes, case, reverse,
 * buffer-overflow pattern, int/hex conversion, and ready-to-paste strings.
 */
@Composable
fun StringToolsScreen(navController: NavController) {
    var input by remember { mutableStateOf("") }
    var stripChars by remember { mutableStateOf("") }
    var bufferLen by remember { mutableStateOf("1024") }

    val ops = remember {
        listOf(
            StrOp("Addslashes") { StringOps.addslashes(it) },
            StrOp("Stripslashes") { StringOps.stripslashes(it) },
            StrOp("Strip spaces") { StringOps.stripSpaces(it) },
            StrOp("lowercase") { it.lowercase() },
            StrOp("UPPERCASE") { it.uppercase() },
            StrOp("RaNdOmCaSe") { StringOps.randomCase(it) },
            StrOp("Reverse") { StringOps.reverse(it) },
        )
    }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "String Lab",
            subtitle = "transform · pattern · reference",
            accent = MatrixGreen
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                PromptLabel("input string", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = "type or paste text…",
                    accent = MatrixGreen,
                    minLines = 2
                )

                Spacer(Modifier.height(18.dp))
                PromptLabel("transforms", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                if (input.isEmpty()) {
                    Hint("// enter text above to see transforms")
                } else {
                    ops.forEach { op ->
                        OutputBox(output = "[${op.label}]\n${op.transform(input)}", isError = false, accent = MatrixGreen)
                        Spacer(Modifier.height(10.dp))
                    }
                }

                Spacer(Modifier.height(8.dp))
                PromptLabel("strip custom chars", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = stripChars,
                    onValueChange = { stripChars = it },
                    placeholder = "chars to remove, e.g. '\"<>",
                    accent = MatrixGreen
                )
                Spacer(Modifier.height(8.dp))
                if (input.isNotEmpty() && stripChars.isNotEmpty()) {
                    OutputBox(output = StringOps.stripCustom(input, stripChars), isError = false, accent = MatrixGreen)
                }

                Spacer(Modifier.height(18.dp))
                PromptLabel("int ↔ hex", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                if (input.isEmpty()) {
                    Hint("// enter a number above (decimal or hex)")
                } else {
                    val toHex = StringOps.intToHex(input)
                    val toInt = StringOps.hexToInt(input)
                    OutputBox(
                        output = "[INT → HEX] ${toHex ?: "not a decimal int"}\n[HEX → INT] ${toInt ?: "not valid hex"}",
                        isError = false,
                        accent = MatrixGreen
                    )
                }

                Spacer(Modifier.height(18.dp))
                PromptLabel("buffer overflow pattern", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                Row(
                    Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("128", "512", "1024", "2048").forEach { n ->
                        FormatChip(n, selected = bufferLen == n, accent = MatrixGreen) { bufferLen = n }
                    }
                }
                Spacer(Modifier.height(8.dp))
                TerminalField(
                    value = bufferLen,
                    onValueChange = { bufferLen = it.filter { c -> c.isDigit() }.take(6) },
                    placeholder = "custom length…",
                    accent = MatrixGreen
                )
                Spacer(Modifier.height(8.dp))
                val count = bufferLen.toIntOrNull() ?: 0
                if (count in 1..50000) {
                    OutputBox(output = StringOps.bufferPattern(count), isError = false, accent = MatrixGreen)
                } else {
                    Hint("// enter a length between 1 and 50000")
                }

                Spacer(Modifier.height(18.dp))
                PromptLabel("useful strings", color = MatrixGreen)
                Spacer(Modifier.height(8.dp))
                StringOps.usefulStrings.forEach { (name, value) ->
                    OutputBox(output = "[$name]\n$value", isError = false, accent = MatrixGreen)
                    Spacer(Modifier.height(10.dp))
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun Hint(text: String) {
    Text(
        text,
        color = TextFaint,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
