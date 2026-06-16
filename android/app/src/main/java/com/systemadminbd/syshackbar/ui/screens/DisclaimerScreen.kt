package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.systemadminbd.syshackbar.R
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.CyberCyan
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TerminalSurfaceHigh
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextPrimary
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import com.systemadminbd.syshackbar.ui.theme.Violet
import com.systemadminbd.syshackbar.ui.theme.WarnAmber

/** First-launch ethical-use gate. The user must accept before entering. */
@Composable
fun DisclaimerScreen(onAccept: () -> Unit) {
    var checked by remember { mutableStateOf(false) }

    HackerBackground {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 56.dp, bottom = 28.dp)
        ) {
            // Brand logo — circular crest
            Box(
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(124.dp)
                    .clip(CircleShape)
                    .border(2.dp, MatrixGreen.copy(alpha = 0.55f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.brand_logo),
                    contentDescription = "SystemAdminBD",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                "coded by systemadminbd",
                color = MatrixGreen,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(22.dp))

            Text(
                "Terms of Use",
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                letterSpacing = 0.5.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Please review and accept before you continue. SysHackBar is built strictly for authorized security work.",
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.5.sp,
                lineHeight = 19.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(24.dp))

            Section(
                Icons.Filled.School,
                MatrixGreen,
                "Educational Purpose",
                "SysHackBar is an educational reference toolkit for authorized penetration testing and security research. Every payload, scanner, and lookup is a publicly documented technique provided for learning."
            )
            Section(
                Icons.Filled.VerifiedUser,
                CyberCyan,
                "Your Responsibility",
                "Use this app only against systems you own or have explicit written permission to test. Unauthorized access to computer systems is illegal in most jurisdictions and may carry serious penalties."
            )
            Section(
                Icons.Filled.Gavel,
                WarnAmber,
                "Limitation of Liability",
                "The developers accept no responsibility for any misuse or damage caused by this app. By continuing, you confirm that you understand and accept these terms in full."
            )
            Section(
                Icons.Filled.Lock,
                Violet,
                "Privacy",
                "No personal data is collected. Network tools call public OSINT endpoints only when you run them, and your saved payloads never leave this device."
            )

            Spacer(Modifier.height(6.dp))

            // Consent checkbox
            Row(
                Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (checked) MatrixGreen else TerminalBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .background(
                        if (checked) MatrixGreen.copy(alpha = 0.07f) else TerminalSurface,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { checked = !checked }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(24.dp)
                        .background(
                            if (checked) MatrixGreen else Color.Transparent,
                            RoundedCornerShape(7.dp)
                        )
                        .border(
                            1.5.dp,
                            if (checked) MatrixGreen else TextFaint,
                            RoundedCornerShape(7.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (checked) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            tint = Color(0xFF03110D),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    "I will only use this app for legal, authorized and educational purposes.",
                    color = if (checked) TextPrimary else TextSecondary,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Primary SaaS-style filled button
            val pressScale by animateFloatAsState(if (checked) 1f else 0.99f, label = "scale")
            Box(
                Modifier
                    .fillMaxWidth()
                    .scale(pressScale)
                    .background(
                        if (checked)
                            Brush.linearGradient(listOf(MatrixGreen, CyberCyan))
                        else
                            Brush.linearGradient(listOf(TerminalSurfaceHigh, TerminalSurfaceHigh)),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(enabled = checked, onClick = onAccept)
                    .padding(vertical = 17.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Accept & Continue",
                    color = if (checked) Color.White else TextFaint,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(Modifier.height(12.dp))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(6.dp)
                        .background(AlertRed, CircleShape)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "For authorized testing & education only",
                    color = TextFaint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.3.sp
                )
            }
        }
    }
}

@Composable
private fun Section(icon: ImageVector, accent: Color, title: String, body: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .border(1.dp, TerminalBorder, RoundedCornerShape(14.dp))
            .background(TerminalSurface, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Box(
            Modifier
                .size(38.dp)
                .background(accent.copy(alpha = 0.12f), RoundedCornerShape(11.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title,
                color = TextPrimary,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                body,
                color = TextSecondary,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.5.sp,
                lineHeight = 17.sp
            )
        }
    }
}
