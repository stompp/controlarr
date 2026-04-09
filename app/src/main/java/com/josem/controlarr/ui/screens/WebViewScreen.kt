package com.josem.controlarr.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.josem.controlarr.data.ServerType
import com.josem.controlarr.data.ServerWithHost
import com.josem.controlarr.viewmodel.ServerViewModel

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(
    viewModel: ServerViewModel,
    serverId: Int,
    onNavigateBack: () -> Unit
) {
    var serverWithHost by remember { mutableStateOf<ServerWithHost?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var progress by remember { mutableFloatStateOf(0f) }
    var webView by remember { mutableStateOf<WebView?>(null) }
    var autoLoginDone by remember { mutableStateOf(false) }

    LaunchedEffect(serverId) {
        serverWithHost = viewModel.getServerWithHostById(serverId)
    }

    BackHandler {
        val wv = webView
        if (wv != null && wv.canGoBack()) {
            wv.goBack()
        } else {
            onNavigateBack()
        }
    }

    val swh = serverWithHost ?: return

    val url = buildString {
        append(swh.baseUrl)
        if (swh.server.apiKey.isNotBlank()) {
            append(if ('?' in swh.baseUrl) "&" else "?")
            append("apikey=${swh.server.apiKey}")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(swh.server.name, maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { webView?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Recargar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = swh.server.type.brandColor.copy(alpha = 0.85f)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (isLoading) {
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mixedContentMode =
                            android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false

                        webViewClient = object : WebViewClient() {
                            override fun onPageStarted(
                                view: WebView?,
                                pageUrl: String?,
                                favicon: Bitmap?
                            ) {
                                isLoading = true
                            }

                            override fun onPageFinished(view: WebView?, pageUrl: String?) {
                                isLoading = false
                                if (!autoLoginDone && swh.server.username.isNotBlank()) {
                                    val js = buildAutoLoginJs(
                                        swh.server.type,
                                        swh.server.username,
                                        swh.server.password
                                    )
                                    if (js != null) {
                                        view?.evaluateJavascript(js, null)
                                        autoLoginDone = true
                                    }
                                }
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean = false

                            override fun onReceivedSslError(
                                view: WebView?,
                                handler: SslErrorHandler?,
                                error: SslError?
                            ) {
                                handler?.proceed()
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                progress = newProgress / 100f
                            }
                        }

                        loadUrl(url)
                        webView = this
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

private fun buildAutoLoginJs(
    type: ServerType,
    username: String,
    password: String
): String? {
    val escapedUser = username.replace("'", "\\'").replace("\\", "\\\\")
    val escapedPass = password.replace("'", "\\'").replace("\\", "\\\\")

    return when (type) {
        ServerType.QBITTORRENT -> """
            (function() {
                var u = document.getElementById('username') || document.querySelector('input[name="username"]');
                var p = document.getElementById('password') || document.querySelector('input[name="password"]');
                if (u && p) {
                    u.value = '$escapedUser';
                    p.value = '$escapedPass';
                    var btn = document.getElementById('login') || document.querySelector('button[type="submit"]') || document.querySelector('input[type="submit"]');
                    if (btn) btn.click();
                }
            })();
        """.trimIndent()

        ServerType.JELLYFIN -> """
            (function() {
                setTimeout(function() {
                    var u = document.querySelector('input#txtManualName') || document.querySelector('input[name="username"]');
                    var p = document.querySelector('input#txtManualPassword') || document.querySelector('input[name="password"]');
                    if (u && p) {
                        var nativeSet = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;
                        nativeSet.call(u, '$escapedUser');
                        u.dispatchEvent(new Event('input', {bubbles: true}));
                        nativeSet.call(p, '$escapedPass');
                        p.dispatchEvent(new Event('input', {bubbles: true}));
                        var btn = document.querySelector('button.raised.button-submit') || document.querySelector('button[type="submit"]');
                        if (btn) btn.click();
                    }
                }, 1000);
            })();
        """.trimIndent()

        ServerType.HOMEASSISTANT -> """
            (function() {
                setTimeout(function() {
                    var root = document.querySelector('home-assistant');
                    if (!root || !root.shadowRoot) return;
                    var authFlow = root.shadowRoot.querySelector('ha-authorize');
                    if (!authFlow || !authFlow.shadowRoot) return;
                    var step = authFlow.shadowRoot.querySelector('ha-auth-flow');
                    if (!step || !step.shadowRoot) return;
                    var sr = step.shadowRoot;
                    var u = sr.querySelector('input[name="username"]');
                    var p = sr.querySelector('input[name="password"]');
                    if (u && p) {
                        u.value = '$escapedUser';
                        u.dispatchEvent(new Event('input', {bubbles: true}));
                        p.value = '$escapedPass';
                        p.dispatchEvent(new Event('input', {bubbles: true}));
                        var btn = sr.querySelector('mwc-button') || sr.querySelector('button[type="submit"]');
                        if (btn) btn.click();
                    }
                }, 2000);
            })();
        """.trimIndent()

        ServerType.SONARR, ServerType.RADARR, ServerType.PROWLARR,
        ServerType.LIDARR, ServerType.READARR, ServerType.BAZARR,
        ServerType.OVERSEERR, ServerType.TAUTULLI -> """
            (function() {
                setTimeout(function() {
                    var u = document.querySelector('input[name="username"]') || document.querySelector('input[type="text"]');
                    var p = document.querySelector('input[name="password"]') || document.querySelector('input[type="password"]');
                    if (u && p) {
                        var nativeSet = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value').set;
                        nativeSet.call(u, '$escapedUser');
                        u.dispatchEvent(new Event('input', {bubbles: true}));
                        nativeSet.call(p, '$escapedPass');
                        p.dispatchEvent(new Event('input', {bubbles: true}));
                        var btn = document.querySelector('button[type="submit"]') || document.querySelector('input[type="submit"]');
                        if (btn) setTimeout(function() { btn.click(); }, 200);
                    }
                }, 1500);
            })();
        """.trimIndent()

        ServerType.OTHER -> null
    }
}
