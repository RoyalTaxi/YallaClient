package uz.ildam.technologies.yalla.android.ui.screens.web

import androidx.compose.runtime.Composable

@Composable
fun WebRoute(
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