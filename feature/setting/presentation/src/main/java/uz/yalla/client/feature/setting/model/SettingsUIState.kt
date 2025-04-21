package uz.yalla.client.feature.setting.model

import uz.yalla.client.feature.setting.components.Language
import uz.yalla.client.feature.setting.components.LanguageType
import uz.yalla.client.feature.settings.R

internal data class SettingsUIState(
    val selectedLanguage: Language? = null,
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