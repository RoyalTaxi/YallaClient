package uz.yalla.client.feature.web

import androidx.compose.runtime.Composable

@Composable
internal fun WebRoute(
    title: String,
    url: String,
    onNavigateBack: () -> Unit
) {
    WebScreen(
        title = title,
        url = url,
        onNavigateBack = onNavigateBack
    )
}