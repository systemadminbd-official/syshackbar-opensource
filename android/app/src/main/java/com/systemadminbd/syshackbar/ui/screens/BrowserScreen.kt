package com.systemadminbd.syshackbar.ui.screens

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.systemadminbd.syshackbar.data.Payload
import com.systemadminbd.syshackbar.data.PayloadLibrary
import com.systemadminbd.syshackbar.ui.theme.AlertRed
import com.systemadminbd.syshackbar.ui.theme.CyberCyan
import com.systemadminbd.syshackbar.ui.theme.MatrixGreen
import com.systemadminbd.syshackbar.ui.theme.TerminalBg
import com.systemadminbd.syshackbar.ui.theme.TerminalBorder
import com.systemadminbd.syshackbar.ui.theme.TerminalSurface
import com.systemadminbd.syshackbar.ui.theme.TerminalSurfaceHigh
import com.systemadminbd.syshackbar.ui.theme.TextFaint
import com.systemadminbd.syshackbar.ui.theme.TextPrimary
import com.systemadminbd.syshackbar.ui.theme.TextSecondary
import com.systemadminbd.syshackbar.ui.theme.Violet
import com.systemadminbd.syshackbar.ui.theme.WarnAmber

private const val START_PAGE = "https://duckduckgo.com"

/** A self-contained dark SysHackBar landing page shown on launch (no Google). */
private const val HOME_HTML = """
<!DOCTYPE html><html><head><meta name="viewport" content="width=device-width,initial-scale=1,maximum-scale=1,user-scalable=no">
<style>
  *{margin:0;padding:0;box-sizing:border-box;}
  html,body{width:100%;min-height:100%;}
  body{background:radial-gradient(circle at 50% 18%,#0c1a18 0%,#070a0b 60%,#040506 100%);
    color:#e6f2ee;font-family:'Courier New',monospace;display:flex;flex-direction:column;
    align-items:center;justify-content:center;text-align:center;
    padding:6vh 5vw;min-height:100vh;overflow-x:hidden;}
  .grid{position:fixed;inset:0;background-image:linear-gradient(#00ff9c0a 1px,transparent 1px),
    linear-gradient(90deg,#00ff9c0a 1px,transparent 1px);background-size:34px 34px;z-index:0;}
  .wrap{position:relative;z-index:1;width:100%;max-width:520px;}
  .badge{font-size:clamp(10px,3.2vw,13px);letter-spacing:0.3em;color:#22d3ee;margin-bottom:5vh;
    border:1px solid #22d3ee55;border-radius:30px;padding:7px 16px;display:inline-block;
    white-space:nowrap;}
  .logo{width:clamp(86px,24vw,118px);height:clamp(86px,24vw,118px);border-radius:50%;margin:0 auto 5vh;
    display:flex;align-items:center;justify-content:center;font-size:clamp(40px,11vw,54px);
    background:radial-gradient(circle,#0f1517,#070a0b);
    border:2px solid #00ff9c;box-shadow:0 0 40px #00ff9c55,inset 0 0 22px #00ff9c33;}
  h1{font-size:clamp(30px,9vw,42px);font-weight:800;letter-spacing:0.05em;line-height:1.05;
    word-break:break-word;}
  h1 .g{color:#00ff9c;text-shadow:0 0 18px #00ff9c88;}
  h1 .w{color:#ffffff;}
  .sub{margin-top:14px;font-size:clamp(12px,3.6vw,15px);color:#22d3ee;letter-spacing:0.04em;}
  .slogan{margin-top:10px;font-size:clamp(11px,3.2vw,13px);color:#7e9690;font-style:italic;}
  .made{margin-top:5vh;font-size:clamp(10px,3vw,12px);letter-spacing:0.15em;color:#4a5c58;}
  .made b{color:#ffb454;}
  .line{margin:4vh auto 0;width:min(140px,40vw);height:2px;
    background:linear-gradient(90deg,transparent,#00ff9c,transparent);}
</style></head>
<body><div class="grid"></div><div class="wrap">
  <div class="badge">&gt;_ PENETRATION TOOLKIT</div>
  <div class="logo">&#x1F3AD;</div>
  <h1><span class="g">Sys</span><span class="w">HackBar</span></h1>
  <div class="sub">Its not just a name, Its a brand!</div>
  <div class="slogan">Inject &middot; Encode &middot; Recon &middot; Exploit</div>
  <div class="line"></div>
  <div class="made">MADE BY <b>TEAM SYSTEMADMINBD</b></div>
</div></body></html>
"""

/** Normalises a typed string into a loadable URL or a search query. */
private fun normalizeUrl(raw: String): String {
    val t = raw.trim()
    if (t.isEmpty()) return START_PAGE
    if (t.startsWith("view-source:") || t.startsWith("javascript:")) return t
    val looksLikeUrl = t.contains("://") || (t.contains('.') && !t.contains(' '))
    return when {
        t.contains("://") -> t
        looksLikeUrl -> "http://$t"
        else -> "https://duckduckgo.com/?q=" + android.net.Uri.encode(t)
    }
}

private fun urlEncode(s: String): String = android.net.Uri.encode(s)
private fun base64(s: String): String =
    android.util.Base64.encodeToString(s.toByteArray(), android.util.Base64.NO_WRAP)
/** Percent-encoded hex (e.g. "AB" -> "%41%42") for injecting into URLs/params. */
private fun toHex(s: String): String =
    s.toByteArray().joinToString("") { "%%%02x".format(it) }

/**
 * The core DH-HackBar style screen: an internal WebView browser with a URL bar
 * and a collapsible payload-injection toolbar that appends SQLi / XSS vectors
 * directly into the address field. For authorized testing only.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun BrowserScreen(navController: NavController) {
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current

    var urlText by remember { mutableStateOf("") }
    var currentUrl by remember { mutableStateOf("") }
    var progress by remember { mutableIntStateOf(0) }
    var activeDialog by remember { mutableStateOf<BrowserDialog?>(null) }

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            setBackgroundColor(android.graphics.Color.BLACK)
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    request?.url?.toString()?.let { view?.loadUrl(it) }
                    return true
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                    url?.let { if (!it.startsWith("data:")) { urlText = it; currentUrl = it } }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    url?.let { if (!it.startsWith("data:")) { urlText = it; currentUrl = it } }
                }
            }
            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    progress = newProgress
                }
            }
            loadDataWithBaseURL(null, HOME_HTML, "text/html", "utf-8", null)
        }
    }

    fun goHome() {
        urlText = ""
        currentUrl = ""
        keyboard?.hide()
        webView.loadDataWithBaseURL(null, HOME_HTML, "text/html", "utf-8", null)
    }

    fun go(target: String) {
        val resolved = normalizeUrl(target)
        urlText = resolved
        keyboard?.hide()
        webView.loadUrl(resolved)
    }

    /** Append a payload to the current URL field (DH-HackBar injection behaviour). */
    fun inject(value: String) {
        urlText = if (urlText.isBlank()) value else urlText.trimEnd() + value
    }

    /** Transform the whole URL field value via an encoder. */
    fun transform(fn: (String) -> String) {
        if (urlText.isNotBlank()) urlText = fn(urlText)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBg)
    ) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))

        // ── Title bar ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sys", color = MatrixGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 0.5.sp)
            Text("HackBar", color = TextPrimary, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 20.sp, letterSpacing = 0.5.sp)
            Spacer(Modifier.weight(1f))
            TitleAction(Icons.Filled.Home, "Home") { goHome() }
            TitleAction(Icons.AutoMirrored.Filled.Undo, "Back") { if (webView.canGoBack()) webView.goBack() }
            TitleAction(Icons.AutoMirrored.Filled.Redo, "Forward") { if (webView.canGoForward()) webView.goForward() }
            TitleAction(Icons.Filled.Edit, "Edit URL") { urlText = currentUrl.ifBlank { urlText } }
            TitleAction(Icons.Filled.RemoveRedEye, "View Source") {
                val base = currentUrl.ifBlank { urlText }
                if (base.isNotBlank()) go("view-source:" + normalizeUrl(base))
            }
            TitleAction(Icons.Filled.MoreVert, "More") { navController.navigate("about") }
        }

        // ── Tall URL box (DH-HackBar style) ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .height(120.dp)
                .background(TerminalSurface, RoundedCornerShape(8.dp))
                .border(1.5.dp, CyberCyan.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = Alignment.TopStart
        ) {
            BasicTextField(
                value = urlText,
                onValueChange = { urlText = it },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = TextPrimary
                ),
                cursorBrush = SolidColor(CyberCyan),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = KeyboardActions(onGo = { go(urlText) }),
                modifier = Modifier.fillMaxSize(),
                decorationBox = { inner ->
                    if (urlText.isEmpty()) {
                        Text("Enter URL here...", color = CyberCyan.copy(alpha = 0.6f), fontFamily = FontFamily.Monospace, fontSize = 14.sp)
                    }
                    inner()
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── Horizontally-scrollable tool rows (slide sideways for more) ──
        Toolbar(
            urlText = urlText,
            onGo = { go(urlText) },
            onReload = { webView.reload() },
            onClear = { urlText = "" },
            onBack = { if (webView.canGoBack()) webView.goBack() },
            onForward = { if (webView.canGoForward()) webView.goForward() },
            onInject = ::inject,
            onTransform = ::transform,
            onTamper = { navController.navigate("tamper") },
            onReplace = { navController.navigate("replacer") },
            onEncoder = { navController.navigate("encoder") },
            onViewSource = {
                val base = currentUrl.ifBlank { urlText }
                if (base.isNotBlank()) go("view-source:" + normalizeUrl(base))
            },
            onCookieEditor = { activeDialog = BrowserDialog.Cookies },
            onJsConsole = { activeDialog = BrowserDialog.JsConsole },
            onUserAgent = { activeDialog = BrowserDialog.UserAgent }
        )

        Spacer(Modifier.height(8.dp))

        if (progress in 1..99) {
            LinearProgressIndicator(
                progress = { progress / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = MatrixGreen,
                trackColor = TerminalSurface
            )
        } else {
            Box(Modifier.fillMaxWidth().height(2.dp).background(TerminalBorder))
        }

        // ── WebView ──
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black)
        )
    }

    when (activeDialog) {
        BrowserDialog.Cookies -> CookieEditorDialog(
            url = currentUrl.ifBlank { urlText },
            onDismiss = { activeDialog = null }
        )
        BrowserDialog.JsConsole -> JsConsoleDialog(
            webView = webView,
            onDismiss = { activeDialog = null }
        )
        BrowserDialog.UserAgent -> UserAgentDialog(
            current = webView.settings.userAgentString,
            onApply = { ua ->
                webView.settings.userAgentString = ua
                webView.reload()
                activeDialog = null
            },
            onDismiss = { activeDialog = null }
        )
        null -> Unit
    }
}

private enum class BrowserDialog { Cookies, JsConsole, UserAgent }

private data class UserAgentPreset(val label: String, val value: String)

private val userAgentPresets = listOf(
    UserAgentPreset("Default", ""),
    UserAgentPreset(
        "Android Chrome",
        "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Mobile Safari/537.36"
    ),
    UserAgentPreset(
        "iPhone Safari",
        "Mozilla/5.0 (iPhone; CPU iPhone OS 17_4 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.4 Mobile/15E148 Safari/604.1"
    ),
    UserAgentPreset(
        "Desktop Chrome",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    ),
    UserAgentPreset(
        "Googlebot",
        "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)"
    )
)

@Composable
private fun DialogShell(title: String, accent: Color, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = TerminalSurface,
            shape = RoundedCornerShape(14.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, accent.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(Modifier.weight(1f))
                    Box(
                        modifier = Modifier.size(30.dp).clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(18.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

@Composable
private fun DialogInput(value: String, onValueChange: (String) -> Unit, placeholder: String, minHeight: androidx.compose.ui.unit.Dp = 44.dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .background(TerminalBg, RoundedCornerShape(8.dp))
            .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 13.sp, lineHeight = 19.sp, color = TextPrimary),
            cursorBrush = SolidColor(CyberCyan),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 13.sp)
                inner()
            }
        )
    }
}

@Composable
private fun DialogButton(label: String, accent: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(38.dp)
            .background(accent.copy(alpha = 0.16f), RoundedCornerShape(8.dp))
            .border(1.dp, accent.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

@Composable
private fun CookieEditorDialog(url: String, onDismiss: () -> Unit) {
    val resolved = remember(url) { if (url.isBlank()) START_PAGE else normalizeUrl(url) }
    var cookies by remember { mutableStateOf(CookieManager.getInstance().getCookie(resolved) ?: "") }
    var draft by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    DialogShell("Cookie Editor", WarnAmber, onDismiss) {
        Text(resolved, color = TextSecondary, fontFamily = FontFamily.Monospace, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(8.dp))
        Text("Current cookies", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 60.dp)
                .background(TerminalBg, RoundedCornerShape(8.dp))
                .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                .verticalScroll(rememberScrollState())
                .padding(10.dp)
        ) {
            Text(
                cookies.ifBlank { "No cookies set for this site." },
                color = if (cookies.isBlank()) TextFaint else MatrixGreen,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
        Spacer(Modifier.height(12.dp))
        Text("Set cookie (name=value)", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        DialogInput(draft, { draft = it }, "session=abc123")
        if (status.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(status, color = MatrixGreen, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DialogButton("Set", MatrixGreen, {
                if (draft.contains("=")) {
                    val cm = CookieManager.getInstance()
                    cm.setCookie(resolved, draft.trim())
                    cm.flush()
                    cookies = cm.getCookie(resolved) ?: ""
                    status = "Cookie set."
                    draft = ""
                } else {
                    status = "Use name=value format."
                }
            }, Modifier.weight(1f))
            DialogButton("Clear all", AlertRed, {
                val cm = CookieManager.getInstance()
                cm.removeAllCookies(null)
                cm.flush()
                cookies = ""
                status = "All cookies cleared."
            }, Modifier.weight(1f))
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun JsConsoleDialog(webView: WebView, onDismiss: () -> Unit) {
    var script by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    DialogShell("JS Console", CyberCyan, onDismiss) {
        Text("Run JavaScript on the current page", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.height(6.dp))
        DialogInput(script, { script = it }, "document.title", minHeight = 80.dp)
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DialogButton("Run", MatrixGreen, {
                if (script.isNotBlank()) {
                    webView.evaluateJavascript(script) { value ->
                        result = value ?: "null"
                    }
                }
            }, Modifier.weight(1f))
            DialogButton("Clear", TextSecondary, { script = ""; result = "" }, Modifier.weight(1f))
        }
        if (result.isNotBlank()) {
            Spacer(Modifier.height(12.dp))
            Text("Result", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
            Spacer(Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 40.dp)
                    .background(TerminalBg, RoundedCornerShape(8.dp))
                    .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(10.dp)
            ) {
                Text(result, color = MatrixGreen, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun UserAgentDialog(current: String?, onApply: (String) -> Unit, onDismiss: () -> Unit) {
    var custom by remember { mutableStateOf("") }

    DialogShell("User-Agent Switcher", Violet, onDismiss) {
        Text("Current", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        Text(
            current?.ifBlank { "Default" } ?: "Default",
            color = TextSecondary,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            userAgentPresets.forEach { preset ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(TerminalBg, RoundedCornerShape(8.dp))
                        .border(1.dp, TerminalBorder, RoundedCornerShape(8.dp))
                        .clickable { onApply(preset.value) }
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(preset.label, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Custom", color = TextFaint, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
        Spacer(Modifier.height(4.dp))
        DialogInput(custom, { custom = it }, "Mozilla/5.0 ...")
        Spacer(Modifier.height(10.dp))
        DialogButton("Apply custom", Violet, { if (custom.isNotBlank()) onApply(custom.trim()) }, Modifier.fillMaxWidth())
    }
}

@Composable
private fun TitleAction(icon: ImageVector, desc: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = desc, tint = TextPrimary, modifier = Modifier.size(20.dp))
    }
}

private val ButtonBg = Color(0xFF101010)
private val ButtonBorder = Color(0xFF3A3A3A)

/** A compact solid button with a black background and grey border. */
@Composable
private fun ToolButton(label: String, accent: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(26.dp)
            .background(ButtonBg, RoundedCornerShape(5.dp))
            .border(1.dp, ButtonBorder, RoundedCornerShape(5.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 10.sp, maxLines = 1)
    }
}

/** A square nav arrow button (yellow `<` / `>`). */
@Composable
private fun ArrowButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .background(ButtonBg, RoundedCornerShape(5.dp))
            .border(1.dp, ButtonBorder, RoundedCornerShape(5.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = WarnAmber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}

private data class InjectMenu(val label: String, val accent: Color, val payloads: List<Payload>)

@Composable
private fun Toolbar(
    urlText: String,
    onGo: () -> Unit,
    onReload: () -> Unit,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onInject: (String) -> Unit,
    onTransform: ((String) -> String) -> Unit,
    onTamper: () -> Unit,
    onReplace: () -> Unit,
    onEncoder: () -> Unit,
    onViewSource: () -> Unit,
    onCookieEditor: () -> Unit,
    onJsConsole: () -> Unit,
    onUserAgent: () -> Unit
) {
    val sqlMenus = remember {
        listOf(
            InjectMenu("Union Based", MatrixGreen, PayloadLibrary.byId("sqli_union")?.payloads ?: emptyList()),
            InjectMenu("String Based", MatrixGreen, PayloadLibrary.byId("sqli_string")?.payloads ?: emptyList()),
            InjectMenu("Error Based", WarnAmber, PayloadLibrary.byId("sqli_error")?.payloads ?: emptyList()),
            InjectMenu("Double Query", WarnAmber, PayloadLibrary.byId("sqli_double")?.payloads ?: emptyList()),
            InjectMenu("XPath", CyberCyan, PayloadLibrary.byId("sqli_xpath")?.payloads ?: emptyList()),
            InjectMenu("MSSQL", CyberCyan, PayloadLibrary.byId("sqli_mssql")?.payloads ?: emptyList()),
            InjectMenu("PostgreSQL", CyberCyan, PayloadLibrary.byId("sqli_postgres")?.payloads ?: emptyList()),
            InjectMenu("Blind", CyberCyan, PayloadLibrary.byId("sqli_blind")?.payloads ?: emptyList()),
        )
    }
    val bypassMenus = remember {
        listOf(
            InjectMenu("Auth Bypass", AlertRed, PayloadLibrary.byId("sqli_authbypass")?.payloads ?: emptyList()),
            InjectMenu("4x / Null / Join", MatrixGreen, PayloadLibrary.byId("sqli_methods")?.payloads ?: emptyList()),
            InjectMenu("Order By Bypass", WarnAmber, PayloadLibrary.byId("sqli_orderby")?.payloads ?: emptyList()),
            InjectMenu("WAF Bypass", AlertRed, PayloadLibrary.byId("sqli_waf")?.payloads ?: emptyList()),
            InjectMenu("DIOS", MatrixGreen, PayloadLibrary.byId("dios")?.payloads ?: emptyList()),
            InjectMenu("DIOS LocalVar", CyberCyan, PayloadLibrary.byId("dios_localvar")?.payloads ?: emptyList()),
        )
    }
    val otherMenus = remember {
        listOf(
            InjectMenu("XSS", Violet, PayloadLibrary.byId("xss")?.payloads ?: emptyList()),
            InjectMenu("LFI", WarnAmber, PayloadLibrary.byId("lfi")?.payloads ?: emptyList()),
            InjectMenu("RFI", AlertRed, PayloadLibrary.byId("rfi")?.payloads ?: emptyList()),
            InjectMenu("RCE", AlertRed, PayloadLibrary.byId("rce")?.payloads ?: emptyList()),
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Row 1 — primary actions
        ScrollRow {
            ArrowButton("<") { onBack() }
            ArrowButton(">") { onForward() }
            ToolButton("Clear", AlertRed) { onClear() }
            ToolButton("Execute", MatrixGreen) { onGo() }
            ToolButton("Reload", TextPrimary) { onReload() }
            ToolButton("View Source", CyberCyan) { onViewSource() }
        }
        // Row 2 — SQL injection menus
        ScrollRow {
            sqlMenus.forEach { MenuChip(it, onInject) }
        }
        // Row 3 — bypass & DIOS menus
        ScrollRow {
            bypassMenus.forEach { MenuChip(it, onInject) }
        }
        // Row 4 — other injection + encoders + tools
        ScrollRow {
            otherMenus.forEach { MenuChip(it, onInject) }
            ToolButton("URL Encode", CyberCyan) { onTransform(::urlEncode) }
            ToolButton("Base64", CyberCyan) { onTransform(::base64) }
            ToolButton("%Hex", CyberCyan) { onTransform(::toHex) }
            ToolButton("Replace", Violet) { onReplace() }
            ToolButton("Encoder", MatrixGreen) { onEncoder() }
            ToolButton("Tamper", WarnAmber) { onTamper() }
        }
        // Row 5 — live browser tools
        ScrollRow {
            ToolIconButton("Cookies", Icons.Filled.Cookie, WarnAmber) { onCookieEditor() }
            ToolIconButton("JS Console", Icons.Filled.Code, CyberCyan) { onJsConsole() }
            ToolIconButton("User-Agent", Icons.Filled.Devices, Violet) { onUserAgent() }
        }
    }
}

/** A compact button with a leading icon for live browser tools. */
@Composable
private fun ToolIconButton(label: String, icon: ImageVector, accent: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .height(26.dp)
            .background(ButtonBg, RoundedCornerShape(5.dp))
            .border(1.dp, ButtonBorder, RoundedCornerShape(5.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(12.dp))
        Text(label, color = accent, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, fontSize = 10.sp, maxLines = 1)
    }
}

/** A single horizontally-scrollable toolbar row — slide sideways to reveal more tools. */
@Composable
private fun ScrollRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
private fun MenuChip(menu: InjectMenu, onInject: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .height(26.dp)
                .background(ButtonBg, RoundedCornerShape(5.dp))
                .border(1.dp, ButtonBorder, RoundedCornerShape(5.dp))
                .clickable { open = true }
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = menu.accent, modifier = Modifier.size(10.dp))
            Text(menu.label, color = menu.accent, fontFamily = FontFamily.Monospace, fontSize = 10.sp, maxLines = 1)
            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null, tint = TextFaint, modifier = Modifier.size(12.dp))
        }
        DropdownMenu(
            expanded = open,
            onDismissRequest = { open = false },
            modifier = Modifier.background(TerminalSurfaceHigh)
        ) {
            menu.payloads.forEach { p ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(p.title, color = TextPrimary, fontFamily = FontFamily.Monospace, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Text(
                                p.value,
                                color = menu.accent.copy(alpha = 0.9f),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    onClick = {
                        onInject(p.value)
                        open = false
                    }
                )
            }
        }
    }
}
