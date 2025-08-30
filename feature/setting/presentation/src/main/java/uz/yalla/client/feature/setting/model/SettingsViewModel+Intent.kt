package uz.yalla.client.feature.setting.model

import uz.yalla.client.feature.setting.intent.SettingsIntent
import uz.yalla.client.feature.setting.intent.SettingsSideEffect

internal fun SettingsViewModel.onIntent(intent: SettingsIntent) = intent {
    when (intent) {
        SettingsIntent.OnClickLanguage -> {
            postSideEffect(SettingsSideEffect.ClickLanguage)
            setChangeLanguageVisibility(true)
        }
        SettingsIntent.OnClickTheme -> {
            postSideEffect(SettingsSideEffect.ClickTheme)
            setChangeThemeVisibility(true)
        }
        SettingsIntent.OnNavigateBack -> postSideEffect(SettingsSideEffect.NavigateBack)
    }
}