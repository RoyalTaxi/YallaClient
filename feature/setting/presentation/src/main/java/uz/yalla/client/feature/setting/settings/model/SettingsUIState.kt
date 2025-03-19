package uz.yalla.client.feature.setting.settings.model

import uz.yalla.client.core.data.local.AppPreferences
import uz.yalla.client.feature.setting.settings.components.Language
import uz.yalla.client.feature.setting.settings.components.LanguageType
import uz.yalla.client.feature.settings.R

internal data class SettingsUIState(
    val selectedLanguage: Language = when (AppPreferences.locale) {
        "uz" -> Language(R.string.uzbek, "uz")
        else -> Language(R.string.russian, "ru")
    },
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