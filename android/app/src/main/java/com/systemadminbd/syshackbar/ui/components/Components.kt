package com.systemadminbd.syshackbar.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBg
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary

/** Subtle animated grid + glow backdrop used app-wide. */
@Composable
fun HackerBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "bg")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(9000, easing = androidx.compose.animation.core.LinearEasing), RepeatMode.Reverse),
        label = "shift"
    )
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TerminalBg)
            .drawBehind {
                val glow = Brush.radialGradient(
                    colors = listOf(MatrixGreen.copy(alpha = 0.10f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width * (0.2f + shift * 0.6f), size.height * 0.12f),
                    radius = size.maxDimension * 0.7f
                )
                drawRect(glow)
                val step = 38f
                val line = TerminalBorder.copy(alpha = 0.22f)
                var x = 0f
                while (x < size.width) {
                    drawLine(line, androidx.compose.ui.geometry.Offset(x, 0f), androidx.compose.ui.geometry.Offset(x, size.height), 1f)
                    x += step
                }
                var y = 0f
                while (y < size.height) {
                    drawLine(line, androidx.compose.ui.geometry.Offset(0f, y), androidx.compose.ui.geometry.Offset(size.width, y), 1f)
                    y += step
                }
            }
    ) {
        content()
    }
}

/** Small uppercase mono label with a leading prompt glyph. */
@Composable
fun PromptLabel(text: String, color: Color = MatrixGreen, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text("›", color = color, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Spacer(Modifier.width(6.dp))
        Text(
            text.uppercase(),
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp
        )
    }
}

/** A copyable payload row with tap-to-copy feedback. */
@Composable
fun PayloadRow(
    title: String,
    value: String,
    note: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        if (copied) accent else TerminalBorder,
        label = "border"
    )

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1100)
            copied = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(9.dp))
            .background(TerminalSurface, RoundedCornerShape(9.dp))
            .clickable {
                clipboard.setText(AnnotatedString(value))
                copied = true
            }
            .padding(horizontal = 11.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(
                Modifier
                    .size(5.dp)
                    .background(accent, RoundedCornerShape(1.5.dp))
            )
            Spacer(Modifier.width(7.dp))
            Text(
                title,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.5.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(
                if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy,
                contentDescription = "Copy",
                tint = if (copied) accent else TextFaint,
                modifier = Modifier.size(13.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            color = accent.copy(alpha = 0.92f),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.5.sp,
            lineHeight = 14.sp
        )
        if (note.isNotEmpty()) {
            Spacer(Modifier.height(3.dp))
            Text(
                "// $note",
                color = TextFaint,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                lineHeight = 13.sp
            )
        }
    }
}

/** A thin divider line in terminal style. */
@Composable
fun TerminalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(TerminalBorder)
    )
}
