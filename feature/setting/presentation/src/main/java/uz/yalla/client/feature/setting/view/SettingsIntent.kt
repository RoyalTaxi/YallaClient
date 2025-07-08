package uz.yalla.client.feature.setting.view

import uz.yalla.client.feature.setting.components.Language

internal sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
    data object OnClickLanguage : SettingsIntent
    data object OnClickTheme : SettingsIntent
}