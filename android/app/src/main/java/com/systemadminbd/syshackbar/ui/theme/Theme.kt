package com.systemadminbd.syshackbar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Terminal / hacker palette
val TerminalBg = Color(0xFF070A0B)
val TerminalSurface = Color(0xFF0F1517)
val TerminalSurfaceHigh = Color(0xFF161E21)
val TerminalBorder = Color(0xFF1F2B2E)
val MatrixGreen = Color(0xFF00FF9C)
val MatrixGreenDim = Color(0xFF14B97A)
val CyberCyan = Color(0xFF22D3EE)
val AlertRed = Color(0xFFFF4D6D)
val WarnAmber = Color(0xFFFFB454)
val Violet = Color(0xFFB388FF)
val TextPrimary = Color(0xFFE6F2EE)
val TextSecondary = Color(0xFF7E9690)
val TextFaint = Color(0xFF4A5C58)

private val HackBarColors = darkColorScheme(
    primary = MatrixGreen,
    onPrimary = TerminalBg,
    secondary = CyberCyan,
    onSecondary = TerminalBg,
    tertiary = Violet,
    background = TerminalBg,
    onBackground = TextPrimary,
    surface = TerminalSurface,
    onSurface = TextPrimary,
    surfaceVariant = TerminalSurfaceHigh,
    onSurfaceVariant = TextSecondary,
    outline = TerminalBorder,
    error = AlertRed,
    onError = TerminalBg,
)

private val Mono = FontFamily.Monospace

private val HackBarType = Typography(
    displaySmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 1.sp),
    headlineSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 0.5.sp),
    titleLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Bold, fontSize = 18.sp),
    titleMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
    bodyLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 13.sp),
    bodySmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Normal, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = Mono, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, letterSpacing = 0.5.sp),
    labelMedium = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 11.sp, letterSpacing = 0.8.sp),
    labelSmall = TextStyle(fontFamily = Mono, fontWeight = FontWeight.Medium, fontSize = 10.sp, letterSpacing = 0.8.sp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HackBarColors,
        typography = HackBarType,
        content = content
    )
}
