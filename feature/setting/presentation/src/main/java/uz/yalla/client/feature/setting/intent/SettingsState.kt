package uz.yalla.client.feature.setting.intent

import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.LanguageType
import uz.yalla.client.feature.setting.components.Theme
import uz.yalla.client.feature.settings.R

internal data class SettingsState(
    val selectedLanguage: Language?,
    val changeLanguageSheetVisibility: Boolean,

    val changeThemeSheetVisibility: Boolean,
    val selectedTheme: Theme?,
    val themes: List<Theme>,
    val languages: List<Language>
) {
    companion object {
        val INITIAL = SettingsState(
            selectedLanguage = null,
            changeLanguageSheetVisibility = false,
            changeThemeSheetVisibility = false,
            selectedTheme = null,
            themes = listOf(
                Theme(
                    stringResId = R.string.light_mode,
                    themeType = ThemeType.LIGHT
                ),
                Theme(
                    stringResId = R.string.dark_mode,
                    themeType = ThemeType.DARK
                ),
                Theme(
                    stringResId = R.string.system_mode,
                    themeType = ThemeType.SYSTEM
                )
            ),
            languages = listOf(
                Language(
                    stringResId = R.string.uzbek,
                    languageTag = LanguageType.UZBEK.languageTag
                ),
                Language(
                    stringResId = R.string.russian,
                    languageTag = LanguageType.RUSSIAN.languageTag
                )
            )
        )
    }
}