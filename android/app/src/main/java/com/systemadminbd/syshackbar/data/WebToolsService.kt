package com.systemadminbd.syshackbar.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/** Outcome of a web-tool run. */
sealed interface ToolResult {
    data class Lines(val items: List<String>) : ToolResult
    data class Text(val body: String) : ToolResult
    data class Error(val message: String) : ToolResult
}

/**
 * Real network operations backing the Web Tools screen.
 * Uses public OSINT endpoints + direct probing. Authorized testing only.
 */
object WebToolsService {

    private const val UA = "SysHackBar/1.0 (educational)"

    private fun normalizeHost(input: String): String =
        input.trim()
            .removePrefix("https://")
            .removePrefix("http://")
            .substringBefore("/")
            .substringBefore(":")

    private suspend fun httpGet(rawUrl: String, timeoutMs: Int = 12000): String =
        withContext(Dispatchers.IO) {
            val conn = (URL(rawUrl).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                instanceFollowRedirects = true
                setRequestProperty("User-Agent", UA)
            }
            try {
                val code = conn.responseCode
                val stream = if (code in 200..399) conn.inputStream else conn.errorStream
                stream?.bufferedReader()?.use { it.readText() } ?: ""
            } finally {
                conn.disconnect()
            }
        }

    /** Reverse-IP: find domains hosted on the same server. */
    suspend fun reverseIp(input: String): ToolResult = try {
        val host = normalizeHost(input)
        if (host.isEmpty()) return ToolResult.Error("Enter a host or IP")
        val body = httpGet("https://api.hackertarget.com/reverseiplookup/?q=${enc(host)}")
        parseListBody(body, "No domains found")
    } catch (e: Exception) {
        ToolResult.Error(e.message ?: "Request failed")
    }

    /** Subdomain / host search for a domain. */
    suspend fun subdomains(input: String): ToolResult = try {
        val host = normalizeHost(input)
        if (host.isEmpty()) return ToolResult.Error("Enter a domain")
        val body = httpGet("https://api.hackertarget.com/hostsearch/?q=${enc(host)}")
        parseListBody(body, "No subdomains found")
    } catch (e: Exception) {
        ToolResult.Error(e.message ?: "Request failed")
    }

    /** DNS records lookup. */
    suspend fun dnsLookup(input: String): ToolResult = try {
        val host = normalizeHost(input)
        if (host.isEmpty()) return ToolResult.Error("Enter a domain")
        val body = httpGet("https://api.hackertarget.com/dnslookup/?q=${enc(host)}")
        parseListBody(body, "No records found")
    } catch (e: Exception) {
        ToolResult.Error(e.message ?: "Request failed")
    }

    /** Probe common admin-panel paths and report reachable ones. */
    suspend fun adminFinder(input: String): ToolResult = withContext(Dispatchers.IO) {
        try {
            val host = normalizeHost(input)
            if (host.isEmpty()) return@withContext ToolResult.Error("Enter a host")
            val base = "http://$host"
            val found = mutableListOf<String>()
            for (path in ADMIN_PATHS) {
                val url = "$base/$path"
                try {
                    val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                        requestMethod = "HEAD"
                        connectTimeout = 5000
                        readTimeout = 5000
                        instanceFollowRedirects = false
                        setRequestProperty("User-Agent", UA)
                    }
                    val code = conn.responseCode
                    conn.disconnect()
                    if (code in intArrayOf(200, 301, 302, 401, 403)) {
                        found.add("[$code] /$path")
                    }
                } catch (_: Exception) { /* skip unreachable */ }
            }
            if (found.isEmpty()) ToolResult.Text("Scanned ${ADMIN_PATHS.size} paths — no common admin panel reachable.")
            else ToolResult.Lines(found)
        } catch (e: Exception) {
            ToolResult.Error(e.message ?: "Scan failed")
        }
    }

    /** Naive SQLi scanner: inject a quote into a URL param and look for SQL errors. */
    suspend fun sqliScan(input: String): ToolResult = try {
        val url = input.trim()
        if (!url.startsWith("http")) return ToolResult.Error("Enter a full URL incl. a parameter, e.g. http://site/p?id=1")
        if (!url.contains("=")) return ToolResult.Error("URL must contain a parameter (…?id=1)")
        val payloadUrl = url + URLEncoder.encode("'", "UTF-8")
        val body = httpGet(payloadUrl).lowercase()
        val hit = SQL_ERRORS.firstOrNull { body.contains(it) }
        if (hit != null) ToolResult.Text("⚠ Possible SQL injection!\nMatched DB error signature:\n\"$hit\"")
        else ToolResult.Text("No obvious SQL error returned.\nTarget may be safe, or needs blind/time-based testing.")
    } catch (e: Exception) {
        ToolResult.Error(e.message ?: "Scan failed")
    }

    /** Whois lookup for a domain or IP. */
    suspend fun whois(input: String): ToolResult = try {
        val host = normalizeHost(input)
        if (host.isEmpty()) return ToolResult.Error("Enter a domain or IP")
        val body = httpGet("https://api.hackertarget.com/whois/?q=${enc(host)}")
        val trimmed = body.trim()
        if (trimmed.isEmpty()) ToolResult.Text("No whois data found")
        else if (trimmed.contains("API count exceeded", true)) ToolResult.Error(trimmed.take(160))
        else ToolResult.Text(trimmed)
    } catch (e: Exception) {
        ToolResult.Error(e.message ?: "Request failed")
    }

    /** Inspect HTTP response headers for a URL. */
    suspend fun headersInspector(input: String): ToolResult =
        withContext(Dispatchers.IO) {
            try {
                var url = input.trim()
                if (url.isEmpty()) return@withContext ToolResult.Error("Enter a URL")
                if (!url.startsWith("http")) url = "https://$url"
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", UA)
                }
                val code = conn.responseCode
                val msg = conn.responseMessage ?: ""
                val out = mutableListOf("HTTP $code $msg")
                conn.headerFields.forEach { (k, v) ->
                    if (k != null) out.add("$k: ${v.joinToString(", ")}")
                }
                conn.disconnect()
                ToolResult.Lines(out)
            } catch (e: Exception) {
                ToolResult.Error(e.message ?: "Request failed")
            }
        }

    /** Scan common TCP ports on a host and report open ones. */
    suspend fun portScan(input: String): ToolResult =
        withContext(Dispatchers.IO) {
            try {
                val host = normalizeHost(input)
                if (host.isEmpty()) return@withContext ToolResult.Error("Enter a host or IP")
                val addr = java.net.InetAddress.getByName(host)
                val open = mutableListOf<String>()
                for ((port, service) in COMMON_PORTS) {
                    try {
                        java.net.Socket().use { socket ->
                            socket.connect(java.net.InetSocketAddress(addr, port), 1200)
                            open.add("$port/tcp  open   $service")
                        }
                    } catch (_: Exception) { /* closed / filtered */ }
                }
                if (open.isEmpty()) ToolResult.Text("Scanned ${COMMON_PORTS.size} common ports on $host — none open/reachable.")
                else ToolResult.Lines(open)
            } catch (e: Exception) {
                ToolResult.Error(e.message ?: "Scan failed")
            }
        }

    /** Fetch HTTP response headers for a URL (Tamper Data inspection). */
    suspend fun fetchHeaders(rawUrl: String, headers: Map<String, String>): ToolResult =
        withContext(Dispatchers.IO) {
            try {
                var url = rawUrl.trim()
                if (!url.startsWith("http")) url = "http://$url"
                val conn = (URL(url).openConnection() as HttpURLConnection).apply {
                    requestMethod = "GET"
                    connectTimeout = 10000
                    readTimeout = 10000
                    instanceFollowRedirects = false
                    setRequestProperty("User-Agent", UA)
                    headers.forEach { (k, v) -> if (k.isNotBlank()) setRequestProperty(k, v) }
                }
                val code = conn.responseCode
                val msg = conn.responseMessage ?: ""
                val out = mutableListOf("HTTP $code $msg", "")
                conn.headerFields.forEach { (k, v) ->
                    if (k != null) out.add("$k: ${v.joinToString(", ")}")
                }
                conn.disconnect()
                ToolResult.Lines(out)
            } catch (e: Exception) {
                ToolResult.Error(e.message ?: "Request failed")
            }
        }

    private fun parseListBody(body: String, emptyMsg: String): ToolResult {
        val trimmed = body.trim()
        if (trimmed.isEmpty()) return ToolResult.Text(emptyMsg)
        if (trimmed.contains("API count exceeded", true) || trimmed.startsWith("error", true)) {
            return ToolResult.Error(trimmed.take(160))
        }
        val lines = trimmed.lines().map { it.trim() }.filter { it.isNotEmpty() }
        return if (lines.isEmpty()) ToolResult.Text(emptyMsg) else ToolResult.Lines(lines)
    }

    private fun enc(s: String): String = URLEncoder.encode(s, "UTF-8")

    private val ADMIN_PATHS = listOf(
        "admin", "administrator", "admin/login", "admin.php", "login",
        "login.php", "wp-admin", "wp-login.php", "user/login", "cpanel",
        "admin/index.php", "adminpanel", "controlpanel", "admin_area",
        "moderator", "webadmin", "adminarea", "panel", "dashboard",
        "admin/admin.php", "phpmyadmin", "manager", "admin1", "admin2"
    )

    private val COMMON_PORTS = listOf(
        21 to "ftp", 22 to "ssh", 23 to "telnet", 25 to "smtp",
        53 to "dns", 80 to "http", 110 to "pop3", 143 to "imap",
        443 to "https", 445 to "smb", 3306 to "mysql", 3389 to "rdp",
        5432 to "postgresql", 6379 to "redis", 8080 to "http-alt",
        8443 to "https-alt", 27017 to "mongodb"
    )

    private val SQL_ERRORS = listOf(
        "you have an error in your sql syntax",
        "warning: mysql", "mysql_fetch", "mysql_num_rows",
        "supplied argument is not a valid mysql",
        "unclosed quotation mark", "quoted string not properly terminated",
        "pg_query", "postgresql query failed", "sqlite_error",
        "sql syntax", "odbc", "ora-01756", "microsoft ole db provider"
    )
}
