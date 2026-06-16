package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary

/** Shared screen frame with a back-arrow header and accent title. */
@Composable
fun ScreenScaffold(
    navController: NavController,
    title: String,
    subtitle: String? = null,
    accent: Color = MatrixGreen,
    content: @Composable () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 6.dp, end = 16.dp, top = 52.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = accent
                )
            }
            Spacer(Modifier.width(2.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (subtitle != null) {
                    Text(
                        subtitle,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
            Box(
                Modifier
                    .size(8.dp)
                    .background(accent, RoundedCornerShape(2.dp))
            )
        }
        content()
    }
}

/** Terminal-styled single/multi-line input field. */
@Composable
fun TerminalField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    accent: Color = MatrixGreen,
    minLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, TerminalBorder, RoundedCornerShape(12.dp))
            .background(TerminalSurface, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp,
                lineHeight = 19.sp
            ),
            cursorBrush = SolidColor(accent),
            minLines = minLines,
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(
                        placeholder,
                        color = TextFaint,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
                inner()
            }
        )
    }
}
