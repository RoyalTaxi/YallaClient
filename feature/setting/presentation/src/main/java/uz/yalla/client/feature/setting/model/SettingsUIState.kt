package uz.yalla.client.feature.setting.model

import uz.yalla.client.core.domain.model.type.ThemeType
import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.LanguageType
import uz.yalla.client.feature.setting.components.Theme
import uz.yalla.client.feature.settings.R

internal data class SettingsUIState(
    val selectedLanguage: Language? = null,
    val changeLanguageSheetVisibility: Boolean = false,

    val changeThemeSheetVisibility: Boolean = false,
    val selectedTheme: Theme? = null,
    val themes: List<Theme> = listOf(
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
    val languages: List<Language> = listOf(
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