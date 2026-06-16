package com.systemadminbd.syshackbar.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.PayloadLibrary
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.components.PayloadRow
import com.systemadminbd.syshackbar.ui.theme.TextFaint

@Composable
fun CategoryScreen(navController: NavController, categoryId: String) {
    val category = PayloadLibrary.byId(categoryId)
    var query by remember { mutableStateOf("") }

    HackerBackground {
        if (category == null) {
            ScreenScaffold(navController = navController, title = "Not found", accent = com.systemadminbd.syshackbar.ui.theme.AlertRed) {}
            return@HackerBackground
        }
        val filtered = category.payloads.filter {
            query.isBlank() ||
                it.title.contains(query, ignoreCase = true) ||
                it.value.contains(query, ignoreCase = true)
        }
        ScreenScaffold(
            navController = navController,
            title = category.name,
            subtitle = category.tagline,
            accent = category.accent
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                item {
                    TerminalField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = "search payloads…",
                        accent = category.accent
                    )
                    Spacer(Modifier.height(4.dp))
                }
                if (filtered.isEmpty()) {
                    item {
                        Text(
                            "// no matches",
                            color = TextFaint,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
                items(filtered) { p ->
                    PayloadRow(
                        title = p.title,
                        value = p.value,
                        note = p.note,
                        accent = category.accent
                    )
                }
                item {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "// tap a payload to copy it",
                        color = TextFaint,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}
