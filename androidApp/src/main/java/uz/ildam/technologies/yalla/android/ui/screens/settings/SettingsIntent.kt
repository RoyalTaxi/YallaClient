package uz.ildam.technologies.yalla.android.ui.screens.settings

import uz.ildam.technologies.yalla.android.ui.screens.language.Language

sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
    data object OnClickLanguage: SettingsIntent
}