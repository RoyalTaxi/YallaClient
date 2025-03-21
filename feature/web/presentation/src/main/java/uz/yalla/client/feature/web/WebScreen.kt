package uz.yalla.client.feature.web

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import uz.yalla.client.core.presentation.design.theme.YallaTheme
import androidx.core.net.toUri

@Composable
internal fun WebScreen(
    title: String,
    url: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
            WebScreenTopBar(
                title = title,
                onNavigateBack = onNavigateBack
            )
        },
        content = { paddingValues ->
            WebContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                url = url,
                onNavigateBack = onNavigateBack
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebScreenTopBar(
    title: String,
    onNavigateBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(YallaTheme.color.white),
        title = {
            Text(
                text = title,
                color = YallaTheme.color.black,
                style = YallaTheme.font.labelLarge
            )
        },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun WebContent(
    modifier: Modifier = Modifier,
    url: String,
    onNavigateBack: () -> Unit
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            createWebView(context, onNavigateBack)
        },
        update = { webView ->
            webView.loadUrl(url)
        }
    )
}

private fun createWebView(context: Context, onNavigateBack: () -> Unit): WebView {
    return WebView(context).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.WHITE)
        webViewClient = createWebViewClient(context, onNavigateBack)
    }
}

private fun createWebViewClient(context: Context, onNavigateBack: () -> Unit): WebViewClient {
    return object : WebViewClient() {
        override fun shouldOverrideUrlLoading(
            view: WebView?,
            request: WebResourceRequest?
        ): Boolean {
            val newUrl = request?.url?.toString() ?: return false

            try {
                val intent = Intent(Intent.ACTION_VIEW, newUrl.toUri())
                onNavigateBack()
                context.startActivity(intent)
                return true
            } catch (_: Exception) {
                return false
            }
        }
    }
}