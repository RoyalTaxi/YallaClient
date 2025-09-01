package uz.yalla.client.feature.setting.intent

sealed interface SettingsSideEffect {
    data object NavigateBack: SettingsSideEffect
    data object ClickLanguage: SettingsSideEffect
    data object ClickTheme: SettingsSideEffect
}