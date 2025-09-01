package uz.yalla.client.feature.setting.intent

internal sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
    data object OnClickLanguage : SettingsIntent
    data object OnClickTheme : SettingsIntent
}