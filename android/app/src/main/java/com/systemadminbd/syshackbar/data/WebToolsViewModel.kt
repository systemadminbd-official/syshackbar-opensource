package com.systemadminbd.syshackbar.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** UI state for a single web-tool run. */
data class WebToolUiState(
    val running: Boolean = false,
    val result: ToolResult? = null
)

/** Drives the networked Web Tools screens. */
class WebToolsViewModel : ViewModel() {

    private val _state = MutableStateFlow(WebToolUiState())
    val state: StateFlow<WebToolUiState> = _state.asStateFlow()

    private var job: Job? = null

    /** Run one of the networked operations identified by [toolId]. */
    fun run(toolId: String, input: String) {
        job?.cancel()
        _state.value = WebToolUiState(running = true, result = null)
        job = viewModelScope.launch {
            val result: ToolResult = when (toolId) {
                "reverse_ip" -> WebToolsService.reverseIp(input)
                "subdomains" -> WebToolsService.subdomains(input)
                "dns" -> WebToolsService.dnsLookup(input)
                "whois" -> WebToolsService.whois(input)
                "headers" -> WebToolsService.headersInspector(input)
                "port_scan" -> WebToolsService.portScan(input)
                "admin_finder" -> WebToolsService.adminFinder(input)
                "sqli_scan" -> WebToolsService.sqliScan(input)
                "hash_crack" -> crackLocally(input)
                else -> ToolResult.Error("Unknown tool")
            }
            _state.value = WebToolUiState(running = false, result = result)
        }
    }

    fun reset() {
        job?.cancel()
        _state.value = WebToolUiState()
    }

    private fun crackLocally(input: String): ToolResult {
        val raw = input.trim()
        if (raw.isEmpty()) return ToolResult.Error("Enter a hash")
        val ids = HashTools.identify(raw)
        val cracked = HashTools.crack(raw)
        val sb = StringBuilder()
        sb.append("Type: ${ids.joinToString(" / ")}\n\n")
        if (cracked != null) sb.append("✓ CRACKED → $cracked")
        else sb.append("✗ Not found in built-in wordlist.\nTry a dedicated offline tool with a larger list.")
        return ToolResult.Text(sb.toString())
    }
}
