package uz.yalla.client.feature.web

import androidx.compose.runtime.Composable

@Composable
 fun WebRoute(
    title: String,
    url: String,
    onBack: () -> Unit
) {
    WebScreen(
        title = title,
        url = url,
        onBack = onBack
    )
}