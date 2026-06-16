package com.systemadminbd.syshackbar.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.CustomPayload
import com.systemadminbd.syshackbar.data.CustomPayloadViewModel
import com.systemadminbd.syshackbar.ui.components.HackerBackground
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import androidx.compose.runtime.collectAsState

@Composable
fun MyPayloadsScreen(navController: NavController) {
    val vm: CustomPayloadViewModel = viewModel()
    val payloads by vm.payloads.collectAsState()

    var editorOpen by remember { mutableStateOf(false) }
    var editId by remember { mutableStateOf<String?>(null) }
    var draftTitle by remember { mutableStateOf("") }
    var draftValue by remember { mutableStateOf("") }

    fun openNew() {
        editId = null
        draftTitle = ""
        draftValue = ""
        editorOpen = true
    }

    fun openEdit(p: CustomPayload) {
        editId = p.id
        draftTitle = p.title
        draftValue = p.value
        editorOpen = true
    }

    HackerBackground {
        ScreenScaffold(
            navController = navController,
            title = "My Payloads",
            subtitle = "${payloads.size} saved",
            accent = MatrixGreen
        ) {
            if (editorOpen) {
                Editor(
                    title = draftTitle,
                    value = draftValue,
                    isEdit = editId != null,
                    onTitle = { draftTitle = it },
                    onValue = { draftValue = it },
                    onCancel = { editorOpen = false },
                    onSave = {
                        val t = draftTitle.trim().ifEmpty { "untitled" }
                        val v = draftValue.trim()
                        if (v.isNotEmpty()) {
                            val id = editId
                            if (id == null) vm.add(t, v) else vm.update(id, t, v)
                        }
                        editorOpen = false
                    }
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .border(1.dp, MatrixGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                .background(MatrixGreen.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                                .clickable { openNew() }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = MatrixGreen, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "NEW PAYLOAD",
                                color = MatrixGreen,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                    if (payloads.isEmpty()) {
                        item {
                            Text(
                                "// nothing saved yet — store your favorite\n// queries here for quick reuse",
                                color = TextFaint,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }
                    }
                    items(payloads, key = { it.id }) { p ->
                        SavedRow(
                            payload = p,
                            onEdit = { openEdit(p) },
                            onDelete = { vm.remove(p.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Editor(
    title: String,
    value: String,
    isEdit: Boolean,
    onTitle: (String) -> Unit,
    onValue: (String) -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
        Text(
            if (isEdit) "// edit payload" else "// new payload",
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp
        )
        Spacer(Modifier.height(12.dp))
        TerminalField(value = title, onValueChange = onTitle, placeholder = "title", accent = MatrixGreen)
        Spacer(Modifier.height(10.dp))
        TerminalField(value = value, onValueChange = onValue, placeholder = "payload / query", accent = MatrixGreen, minLines = 4)
        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                Modifier
                    .weight(1f)
                    .border(1.dp, TerminalBorder, RoundedCornerShape(10.dp))
                    .clickable(onClick = onCancel)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Close, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("CANCEL", color = TextSecondary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Row(
                Modifier
                    .weight(1f)
                    .background(MatrixGreen, RoundedCornerShape(10.dp))
                    .clickable(onClick = onSave)
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = MaterialTheme.colorScheme.background, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("SAVE", color = MaterialTheme.colorScheme.background, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SavedRow(payload: CustomPayload, onEdit: () -> Unit, onDelete: () -> Unit) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }
    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(1100)
            copied = false
        }
    }
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, if (copied) MatrixGreen else TerminalBorder, RoundedCornerShape(12.dp))
            .background(TerminalSurface, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(Modifier.size(7.dp).background(MatrixGreen, RoundedCornerShape(2.dp)))
            Spacer(Modifier.width(8.dp))
            Text(
                payload.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )
            IconBtn(if (copied) Icons.Filled.Check else Icons.Filled.ContentCopy, if (copied) MatrixGreen else TextFaint) {
                clipboard.setText(AnnotatedString(payload.value))
                copied = true
            }
            Spacer(Modifier.width(4.dp))
            IconBtn(Icons.Filled.Edit, TextFaint, onEdit)
            Spacer(Modifier.width(4.dp))
            IconBtn(Icons.Filled.Delete, AlertRed.copy(alpha = 0.8f), onDelete)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            payload.value,
            color = MatrixGreen.copy(alpha = 0.9f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun IconBtn(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(30.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
    }
}
