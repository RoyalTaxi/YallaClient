package uz.yalla.client.feature.setting.settings.view

import uz.yalla.client.feature.setting.settings.components.Language

internal sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
    data object OnClickLanguage : SettingsIntent
    data class OnUpdateLanguage(val language: Language) : SettingsIntent
}