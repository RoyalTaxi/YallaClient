package uz.ildam.technologies.yalla.android.ui.screens.settings

import androidx.compose.runtime.Composable

@Composable
fun SettingsRoute(
    onNavigateBack: () -> Unit
) {
    SettingsScreen(
        onIntent = { intent ->
            when (intent) {
                SettingsIntent.OnNavigateBack -> onNavigateBack()
            }
        }
    )
}