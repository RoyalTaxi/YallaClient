package uz.yalla.client.feature.android.setting.settings.view

interface sealed interface SettingsIntent {
    data object OnNavigateBack : SettingsIntent
    data object OnClickLanguage: SettingsIntent
}