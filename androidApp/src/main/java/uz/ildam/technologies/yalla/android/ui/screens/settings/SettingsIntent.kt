package uz.ildam.technologies.yalla.android.ui.screens.settings

sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
}