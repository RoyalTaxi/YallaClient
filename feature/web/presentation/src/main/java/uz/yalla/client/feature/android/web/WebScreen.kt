package uz.yalla.client.feature.android.web

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WebScreen(
    title: String,
    url: String,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = YallaTheme.color.white,
        topBar = {
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
        },
        content = { paddingValues ->
            AndroidView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                factory = { context ->
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        setBackgroundColor(Color.WHITE)
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView?,
                                request: WebResourceRequest?
                            ): Boolean {
                                val newUrl = request?.url?.toString()
                                if (newUrl != null) {
                                    try {
                                        val intent = Intent(Intent.ACTION_VIEW, newUrl.toUri())
                                        onNavigateBack()
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                    }
                                    return true
                                }
                                return false
                            }
                        }
                    }
                },
                update = {
                    it.loadUrl(url)
                }
            )
        }
    )
}